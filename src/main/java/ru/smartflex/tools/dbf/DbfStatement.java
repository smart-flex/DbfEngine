package ru.smartflex.tools.dbf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * The class <code>DbfStatement</code> is used for record preparation and insertion.
 *
 * @author galisha
 * @since 1.00
 */
public class DbfStatement {

    private byte[] rec;
    private Map<String, DbfColumnPosition> mapColumnPos = new HashMap<String, DbfColumnPosition>();
    private DbfHeader dbfHeader;
    private DbfCodePages dbfCodePage;
    private File tempDbf = null;
    private OutputStream dbfStream = null;
    private DbfAppender dbfAppender;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    private byte[] oneByte = new byte[1];

    DbfStatement(DbfHeader dbfHeader, DbfCodePages dbfCodePage,
                           DbfAppender dbfAppender) {
        this.dbfHeader = dbfHeader;
        this.dbfCodePage = dbfCodePage;
        this.dbfAppender = dbfAppender;
        rec = new byte[dbfHeader.getLengthRecord()];
        clearStatement();
        createTempFile();
    }

    private void createTempFile() {
        try {
            tempDbf = File.createTempFile("engine", "dbf");
            dbfStream = new FileOutputStream(tempDbf);
        } catch (FileNotFoundException e) {
            throw new DbfEngineException(DbfConstants.EXCP_DBF_ERR_CREATE, e);
        } catch (IOException e) {
            throw new DbfEngineException(DbfConstants.EXCP_IO_ERROR, e);

        }
    }

    /**
     * Marks current record as deleted.
     *
     * @since 1.00
     */
    public void markAsDeleted() {
        rec[0] = DbfConstants.DELETED_MARKER;
    }

    InputStream getDbfBodyInputStream() {
        try {
            dbfStream.close();
            return new FileInputStream(tempDbf);
        } catch (IOException e) {
            throw new DbfEngineException(DbfConstants.EXCP_IO_ERROR, e);
        }
    }

    /**
     * Insert current record into dbf file.
     *
     * @since 1.00
     */
    public void insertStatement() {
        try {
            dbfStream.write(rec);
            dbfStream.flush();
            dbfAppender.registerNewRecord();
        } catch (IOException e) {
            throw new DbfEngineException(DbfConstants.EXCP_IO_ERROR, e);
        }

        clearStatement();
    }

    private void clearStatement() {
        for (int i = 0; i < rec.length; i++) {
            rec[i] = DbfConstants.DBF_REC_FILLSYMB;
        }
    }

    /**
     * Sets the designated parameter to the given Java <code>String</code> value
     *
     * @param colName xBase field name
     * @param value   value
     * @since 1.00
     */
    public void setString(String colName, String value) {
        if (value != null) {
            DbfColumnPosition dcp = definePosition(colName,
                    DbfColumnTypes.Character);
            if (value.length() > dcp.getColumnLength()) {
                throw new DbfEngineException(
                        DbfConstants.EXCP_COLUMN_VAL_TOO_BIG + colName);
            }
            try {
                byte[] val = value.getBytes(dbfCodePage.getCharsetName());
                fillRec(dcp, val);
            } catch (UnsupportedEncodingException e) {
                throw new DbfEngineException(DbfConstants.EXCP_COLUMN_CP, e);
            }
        }
    }

    /**
     * Sets the designated parameter to the given Java <code>Date</code> value
     *
     * @param colName xBase field name
     * @param value   value
     * @since 1.00
     */
    public void setDate(String colName, Date value) {
        if (value != null) {
            DbfColumnPosition dcp = definePosition(colName, DbfColumnTypes.Date);
            String dateStr = sdf.format(value);
            byte[] val = dateStr.getBytes();
            fillRec(dcp, val);
        }
    }

    /**
     * Sets the designated parameter to the given Java <code>BigDecimal</code> value
     *
     * @param colName xBase field name
     * @param value   value
     * @since 1.00
     */
    public void setBigDecimal(String colName, BigDecimal value) {
        if (value != null) {
            setNumeric(colName, value);
        }
    }

    /**
     * Sets the designated parameter to the given Java <code>Float</code> value
     *
     * @param colName xBase field name
     * @param value   value
     * @since 1.00
     */
    public void setFloat(String colName, Float value) {
        if (value != null) {
            BigDecimal valueBig = new BigDecimal(value.toString());
            setNumeric(colName, valueBig);
        }
    }

    /**
     * Sets the designated parameter to the given Java <code>Boolean</code> value
     *
     * @param colName xBase field name
     * @param value   value
     * @since 1.00
     */
    public void setBoolean(String colName, Boolean value) {
        if (value != null) {
            DbfColumnPosition dcp = definePosition(colName,
                    DbfColumnTypes.Logical);
            if (value.booleanValue()) {
                oneByte[0] = DbfConstants.LOGICAL_TRUE;
            } else {
                oneByte[0] = DbfConstants.LOGICAL_FALSE;
            }
            fillRec(dcp, oneByte);
        }
    }

    private void setNumeric(String colName, BigDecimal value) {
        DbfColumnPosition dcp = definePosition(colName, DbfColumnTypes.Numeric);
        BigDecimal bigval = value;
        if (dcp.getColumnDotAmount() > 0) {
            try {
                bigval = value.setScale(dcp.getColumnDotAmount());
            } catch (ArithmeticException e) {
                throw new DbfEngineException(DbfConstants.EXCP_CP_ARITHERR, e);
            }
        }
        String numStr = bigval.toString();
        if (numStr.length() > dcp.getColumnLength()) {
            throw new DbfEngineException(DbfConstants.EXCP_COLUMN_VAL_TOO_BIG
                    + colName);
        }
        byte[] val = numStr.getBytes();
        fillRecInverse(dcp, val);
    }

    private void fillRecInverse(DbfColumnPosition dcp, byte[] val) {
        int k = val.length - 1;
        for (int i = dcp.getOffset() + dcp.getColumnLength() - 1; i >= dcp
                .getOffset(); i--) {
            if (k >= 0) {
                rec[i] = val[k];
            } else {
                break;
            }
            k--;
        }
    }

    private void fillRec(DbfColumnPosition dcp, byte[] val) {
        int k = 0;
        for (int i = dcp.getOffset(); i < dcp.getOffset()
                + dcp.getColumnLength(); i++) {
            if (k < val.length) {
                rec[i] = val[k];
            } else {
                break;
            }
            k++;
        }
    }

    private DbfColumnPosition definePosition(String colName, DbfColumnTypes dct) {
        DbfColumnPosition dbfColumnPos = mapColumnPos
                .get(colName.toUpperCase());
        if (dbfColumnPos == null) {
            DbfColumn dbfColumn = dbfHeader.getColumn(colName.toUpperCase());
            if (dbfColumn == null) {
                throw new DbfEngineException(DbfConstants.EXCP_COLUMN_NOEXISTS
                        + colName);
            }
            if (dbfColumn.getDbfColumnType().compareTo(dct) != 0) {
                throw new DbfEngineException(DbfConstants.EXCP_COLUMN_COL_TYPE
                        + colName);
            }
            dbfColumnPos = dbfColumn.getDbfColumnPosition();

            mapColumnPos.put(colName.toUpperCase(), dbfColumnPos);
        }
        return dbfColumnPos;
    }

}
