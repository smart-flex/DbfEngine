package ru.smartflex.tools.dbf;

import java.io.*;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Constructs dbf header from set of columns and gives DbfStatement object for record writing.
 *
 * @author galisha
 * @since 1.00
 */
public class DbfAppender {
    private DbfHeader dbfHeader = null;
    private DbfCodePages dbfCodePage;
    private int recordAmount = 0;

    private static transient Calendar calendar = Calendar.getInstance();
    private static Lock lockCalendar = new ReentrantLock(false);

    private OutputStream dbfStream = null;
    private DbfStatement dbfStatement = null;

    private File dbfFileExisted = null;
    private boolean flagAppendExistedFile = false;

    DbfAppender(File dbfFile, DbfCodePages dbfCodePage) {
        this.dbfCodePage = dbfCodePage;
        if (dbfCodePage == null) {
            throw new DbfEngineException(DbfConstants.EXCP_CP_MISSED);
        }
        createOutputStream(dbfFile, false);
    }

    /**
     * Constructor for existed file
     * @param dbfFile dbf file
     * @param dbfHeader header of dbf file
     * @since 1.11
     */
    DbfAppender(File dbfFile, DbfHeader dbfHeader) {
        this.dbfFileExisted = dbfFile;
        this.dbfHeader = dbfHeader;
        this.dbfCodePage = dbfHeader.getDbfCodePages();
        flagAppendExistedFile = true;
    }

    private void createOutputStream(File dbfFile, boolean append) {
        if (dbfStream == null) {
            try {
                dbfStream = new FileOutputStream(dbfFile, append);
            } catch (FileNotFoundException e) {
                throw new DbfEngineException(DbfConstants.EXCP_DBF_ERR_CREATE,
                        e);
            }
        }
    }

    DbfAppender(OutputStream dbfStream, DbfCodePages dbfCodePage) {
        this.dbfCodePage = dbfCodePage;
        if (dbfCodePage == null) {
            throw new DbfEngineException(DbfConstants.EXCP_CP_MISSED);
        }
        this.dbfStream = dbfStream;
    }

    /**
     * Defines columns for dbf file.
     *
     * @param dbfColumns array of dbf columns
     * @since 1.00
     */
    public void defineColumns(DbfColumn... dbfColumns) {
        if (flagAppendExistedFile) {
            throw new DbfEngineException(DbfConstants.EXCP_DEF_COLS_NOT_ALLOWED);
        }
        if (dbfColumns.length == 0) {
            throw new DbfEngineException(DbfConstants.EXCP_COLUMN_ADD);
        }
        int offset = 1;
        int prevLen = 0;
        int cntColumns = 0;
        for (DbfColumn dc : dbfColumns) {
            if (dc != null) {
                cntColumns++;
                if (dbfHeader == null) {
                    dbfHeader = new DbfHeader();
                }
                offset = offset + prevLen;
                dc.getDbfColumnPosition().setOffset(offset);
                dbfHeader.addColumn(dc);
                prevLen = dc.getDbfColumnPosition().getColumnLength();
            }
        }
        if (cntColumns == 0) {
            throw new DbfEngineException(DbfConstants.EXCP_COLUMN_ADD);
        }
    }

    /**
     * Writes records into dbf file and then closes it.
     *
     * @since 1.00
     */
    public void writeDbfAndClose() {
        if (flagAppendExistedFile) {
            writeDbfAndCloseForAppendMode();
        } else {
            writeDbfAndCloseForWriteMode();
        }
    }

    private void writeDbfAndCloseForWriteMode() {

        if (dbfHeader == null) {
            throw new DbfEngineException(DbfConstants.EXCP_COLUMN_ADD);
        }

        byte[] header = new byte[DbfConstants.DBF_HEADER_LENGTH];
        for (int i = 0; i < header.length; i++) {
            header[i] = 0;
        }
        // typedbf
        header[0] = (byte) dbfHeader.getTypeDbf();
        // write date change
        lockCalendar.lock();
        try {
            calendar.setTime(new Date());
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR) % 100;
            header[1] = (byte) year;
            header[2] = (byte) month;
            header[3] = (byte) day;
        } finally {
            lockCalendar.unlock();
        }
        // write record amount
        header[4] = (byte) recordAmount;
        header[5] = (byte) (recordAmount >> 8);
        header[6] = (byte) (recordAmount >> 16);
        header[7] = (byte) (recordAmount >> 24);

        // code page
        header[29] = 0;
        if (dbfCodePage != null) {
            header[29] = (byte) dbfCodePage.getDbfCode();
        }
        // fields
        List<DbfColumn> listColumns = dbfHeader.getOrderedColumnList();
        if (listColumns.size() == 0) {
            throw new DbfEngineException(DbfConstants.EXCP_COLUMN_ADD);
        }
        int offset = 1;
        byte[] columns = new byte[DbfConstants.DBF_COLUMN_LENGTH
                * listColumns.size()];
        for (int i = 0; i < columns.length; i++) {
            columns[i] = 0;
        }
        int indCol = 0;
        for (DbfColumn dcp : listColumns) {
            // fill name
            byte[] colName = dcp.getColumnName().getBytes();
            for (int i = 0; i < DbfConstants.DBF_COLUMN_LENGTH; i++) {
                if (i < colName.length) {
                    columns[i + indCol * DbfConstants.DBF_COLUMN_LENGTH] = colName[i];
                }
            }
            // fill type
            columns[11 + indCol * DbfConstants.DBF_COLUMN_LENGTH] = dcp
                    .getDbfColumnType().getColumnType().getBytes()[0];
            // offset
            columns[12 + indCol * DbfConstants.DBF_COLUMN_LENGTH] = (byte) offset;
            columns[13 + indCol * DbfConstants.DBF_COLUMN_LENGTH] = (byte) (offset >> 8);
            columns[14 + indCol * DbfConstants.DBF_COLUMN_LENGTH] = (byte) (offset >> 16);
            columns[15 + indCol * DbfConstants.DBF_COLUMN_LENGTH] = (byte) (offset >> 24);
            // width
            columns[16 + indCol * DbfConstants.DBF_COLUMN_LENGTH] = (byte) dcp
                    .getDbfColumnPosition().getColumnLength();
            // dec
            columns[17 + indCol * DbfConstants.DBF_COLUMN_LENGTH] = (byte) dcp
                    .getDbfColumnPosition().getColumnDotAmount();

            indCol++;

        }
        // **** header again ****
        // first record position
        int frp = header.length + columns.length + 1;
        header[8] = (byte) frp;
        header[9] = (byte) (frp >> 8);
        // length
        header[10] = (byte) dbfHeader.getLengthRecord();
        header[11] = (byte) (dbfHeader.getLengthRecord() >> 8);

        writeDbf(header, columns);

    }

    void registerNewRecord() {
        recordAmount++;
    }

    private void writeDbfAndCloseForAppendMode() {
        if (recordAmount == 0) {
            // no one record were added. Therefore return.
            return;
        }

        byte[] header = new byte[7];
        lockCalendar.lock();
        try {
            calendar.setTime(new Date());
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR) % 100;
            header[0] = (byte) year;
            header[1] = (byte) month;
            header[2] = (byte) day;
        } finally {
            lockCalendar.unlock();
        }
        int totalRecords = dbfHeader.getCountRecords() + recordAmount;
        header[3] = (byte) totalRecords;
        header[4] = (byte) (totalRecords >> 8);
        header[5] = (byte) (totalRecords >> 16);
        header[6] = (byte) (totalRecords >> 24);

        // change dbf header of existed file
        RandomAccessFile randomFile = null;
        try {
            randomFile = new RandomAccessFile(dbfFileExisted, "rw");
            randomFile.seek(1);
            randomFile.write(header);
        } catch (FileNotFoundException e) {
            throw new DbfEngineException(DbfConstants.EXCP_DBF_NOT_EXISTS, e);
        } catch (IOException e) {
            throw new DbfEngineException(DbfConstants.EXCP_IO_ERROR, e);
        } finally {
            if (randomFile != null) {
                try {
                    randomFile.close();
                } catch (Exception e) {
                    throw new DbfEngineException(DbfConstants.EXCP_IO_ERROR, e);
                }
            }
        }

        createOutputStream(dbfFileExisted, true);

        writeDbf(null, null);
    }

    private void writeDbf(byte[] header, byte[] columns) {
        try {
            if (!flagAppendExistedFile) {
                dbfStream.write(header);
                dbfStream.write(columns);
                dbfStream.write(DbfConstants.DBF_END_HEADER);
            }

            if (dbfStatement != null) {
                InputStream dbfBody = dbfStatement.getDbfBodyInputStream();
                final int recordLength = dbfHeader.getLengthRecord();
                byte[] rec = new byte[recordLength];
                boolean flag = true;
                do {
                    // 08.01.2020 fix bug with stream reading; git:frankvdh
                    int numLeft = recordLength;
                    int offs = 0;
                    while (numLeft > 0) {
                        int numRead = dbfBody.read(rec, offs, numLeft);
                        if (numRead == -1) {
                            flag = false;
                            break;
                        }
                        offs += numRead;
                        numLeft -= numRead;
                    }
                    if (flag) {
                        dbfStream.write(rec, 0, recordLength);
                    }
                } while (flag);
                dbfBody.close();
            }

            dbfStream.flush();
            dbfStream.close();
        } catch (IOException e) {
            throw new DbfEngineException(DbfConstants.EXCP_IO_ERROR, e);
        }
    }

    /**
     * Returns DbfStatement object for adding records.
     *
     * @return statement object
     * @since 1.00
     */
    public DbfStatement getStatement() {
        if (dbfStatement == null) {
            dbfStatement = new DbfStatement(dbfHeader, dbfCodePage, this);
        }
        return dbfStatement;
    }
}
