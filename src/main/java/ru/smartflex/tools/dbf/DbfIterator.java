package ru.smartflex.tools.dbf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * An iterator over xBase file. Reads all file from first record to last record.
 *
 * @author galisha
 * @since 1.00
 */
public class DbfIterator {

    private InputStream dbfStream;
    private int currentRecord = 0;
    private int countRecord = -1;
    private DbfRecord dbfRecord = null;
    private byte[] record = null;
    private int lengthRecord = 0;
    private static final int NON_FILLED_FLAG = -10;

    private int finishOrDeleteFLag = NON_FILLED_FLAG;

    private DbfHeader dbfHeader;

    DbfIterator(File dbfFile, String enc) {
        dbfHeader = new DbfHeader(dbfFile, enc, this);
        handleDbfHeader(dbfHeader);
        this.dbfStream = dbfHeader.getDbfStream();
    }

    DbfIterator(InputStream is, String enc) {
        this.dbfStream = is;
        dbfHeader = new DbfHeader(is, enc, this);
        handleDbfHeader(dbfHeader);
    }

    /**
     * Returns DBF file header definition
     *
     * @return DBF file header definition
     * @since 1.05
     */
    DbfHeader getDbfHeader() {
        return dbfHeader;
    }

    private void handleDbfHeader(DbfHeader dbfHeader) {
        countRecord = dbfHeader.getCountRecords();
        lengthRecord = dbfHeader.getLengthRecord();

        if (lengthRecord > 0) {
            record = new byte[lengthRecord];
        } else {
            throw new DbfEngineException(DbfConstants.EXCP_HEADER_LEN);
        }

        dbfRecord = new DbfRecord(record, dbfHeader);
    }

    /**
     * Returns <code>true</code> if the iteration has more records
     *
     * @return <code>true</code> if the iteration has more records
     * @since 1.00
     */
    public boolean hasMoreRecords() {
        boolean ok = false;
        if (currentRecord < this.countRecord) {
            ok = true;
        }
        if (ok) {
            if (finishOrDeleteFLag == NON_FILLED_FLAG) {
                // non filled flag. Then read it
                // checks finishing byte of file
                try {
                    finishOrDeleteFLag = dbfStream.read() & 0xff;
                } catch (IOException e) {
                    throw new DbfEngineException(DbfConstants.EXCP_IO_ERROR, e);
                }
            }
            // flag was read
            if (finishOrDeleteFLag == -1) {
                ok = false;
            } else {
                if (finishOrDeleteFLag == DbfConstants.DBF_END_OF_FILE) {
                    ok = false;
                }
            }
        }
        return ok;
    }

    /**
     * Returns the next record in the iteration.
     *
     * @return returns the next record in the iteration.
     * @since 1.00
     */
    public DbfRecord nextRecord() {

        if (!hasMoreRecords()) {
            return null;
        } else {
            dbfRecord.setCurrentRecord(currentRecord);
            try {
                record[0] = (byte) finishOrDeleteFLag;
                int numLeft = lengthRecord - 1;
                int offs = 1;
                while (numLeft > 0) {
                    int numRead = dbfStream.read(record, offs, numLeft);
                    if (numRead == -1) {
                        dbfStream.close();
                        throw new DbfEngineException(
                                DbfConstants.EXCP_REC_UNEXP + currentRecord);
                    }
                    offs += numRead;
                    numLeft -= numRead;
                }
                currentRecord++;
                if (currentRecord >= this.countRecord) {
                    dbfStream.close();
                }

                finishOrDeleteFLag = NON_FILLED_FLAG;
            } catch (IOException e) {
                throw new DbfEngineException(DbfConstants.EXCP_IO_ERROR, e);
            }
            return dbfRecord;
        }
    }

    /**
     * The method has to be invoked in case when not all records were read.
     */
    public void closeIterator() {
        if (dbfStream != null) {
            try {
                dbfStream.close();
            } catch (IOException e) {
                dbfStream = null;
            } finally {
                dbfStream = null;
            }
        }
    }

    /**
     * The method returns number of current record.
     *
     * @return current record number
     * @since 1.08
     */
    public int getCurrentRecord() {
        return currentRecord;
    }
}
