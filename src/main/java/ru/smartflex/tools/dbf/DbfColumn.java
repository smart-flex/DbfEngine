package ru.smartflex.tools.dbf;

/**
 * The class <code>DbfColumn</code> is responsible for xBase field definition.
 *
 * @author galisha
 * @since 1.00
 */
public class DbfColumn {

    private String originalType = null;
    private String columnName = null;

    private DbfHeader dbfHeader = null;
    private DbfColumnPosition dbfColumnPosition = null;
    private DbfColumnTypes dbfColumnType = null;

    DbfColumn(DbfHeader dbfHeader) {
        this.dbfHeader = dbfHeader;
    }

    /**
     * Constructs a newly allocated <code>DbfColumn</code> object that
     * represents xBase field.
     *
     * @param columnName    column name
     * @param dbfColumnType enumeration for column type
     * @param width         column width
     * @param dec           column scale ( in other words amount point after comma )
     * @since 1.00
     */
    public DbfColumn(String columnName, DbfColumnTypes dbfColumnType,
                     int width, int dec) {
        if (columnName == null || columnName.trim().length() == 0) {
            throw new DbfEngineException(DbfConstants.EXCP_COLUMN_EMPTY);
        }
        if (columnName.trim().length() > DbfConstants.DBF_COLUMN_NAME_LENGTH) {
            throw new DbfEngineException(DbfConstants.EXCP_COLUMN_NAMELEN);
        }
        if (dbfColumnType == null) {
            throw new DbfEngineException(DbfConstants.EXCP_COLUMN_NOTYPE);
        }
        this.columnName = columnName.trim().toUpperCase();
        this.dbfColumnType = dbfColumnType;

        if (dbfColumnType.compareTo(DbfColumnTypes.Date) == 0) {
            dbfColumnPosition = new DbfColumnPosition(8, 0);
        } else if (dbfColumnType.compareTo(DbfColumnTypes.Logical) == 0) {
            dbfColumnPosition = new DbfColumnPosition(1, 0);
        } else if (dbfColumnType.compareTo(DbfColumnTypes.Character) == 0) {
            if (dec > 0) {
                throw new DbfEngineException(DbfConstants.EXCP_COLUMN_NODEC);
            }
            if (width <= 0 || width > DbfConstants.DBF_COLUMN_CHAR_MAX_LEN) {
                throw new DbfEngineException(DbfConstants.EXCP_COLUMN_CHARLEN);
            }
            dbfColumnPosition = new DbfColumnPosition(width, 0);
        } else if (dbfColumnType.compareTo(DbfColumnTypes.Numeric) == 0
                || dbfColumnType.compareTo(DbfColumnTypes.Float) == 0) {
            if (width <= 0 || width > DbfConstants.DBF_COLUMN_NUM_MAX_LEN) {
                throw new DbfEngineException(DbfConstants.EXCP_COLUMN_NUMLEN);
            }
            if (dec < 0 && (width - dec) < 2) {
                throw new DbfEngineException(DbfConstants.EXCP_COLUMN_NUMDECLEN);
            }
            dbfColumnPosition = new DbfColumnPosition(width, dec);
        }
    }

    /**
     * Returns type of column.
     *
     * @return type of column
     * @since 1.05
     */
    public DbfColumnTypes getDbfColumnType() {
        return dbfColumnType;
    }

    void parse(byte[] recField) {
        originalType = new String(recField, 11, 1);

        int columnLength = recField[16] & 0xff;

        // 16.03.2008 workaround for such dbf where not found offset
        int offset = dbfHeader.getCurrentOffset();

        dbfHeader.setCurrentOffset(offset + columnLength);

        int columnDotAmount = recField[17] & 0xff;
        for (int i = 0; i <= 10; i++) {
            if (recField[i] == 0) {
                // 08.01.2020 fix case for dbf in Shapefile which has columns in lower case; git:frankvdh
                columnName = new String(recField, 0, i).toUpperCase();
                break;
            }
        }
        dbfColumnPosition = new DbfColumnPosition(offset, columnLength,
                columnDotAmount);

        dbfColumnType = DbfColumnTypes.Character.getByOriginalType(originalType);
    }

    /**
     * Returns column name.
     *
     * @return column name
     * @since 1.05
     */
    public String getColumnName() {
        return columnName;
    }

    DbfColumnPosition getDbfColumnPosition() {
        return dbfColumnPosition;
    }

    String getOriginalType() {
        return originalType;
    }

    /**
     * toString method
     *
     * @since 1.07
     */
    @Override
    public String toString() {
        return "DbfColumn [columnName=" + columnName + ", dbfColumnPosition="
                + dbfColumnPosition + ", dbfColumnType=" + dbfColumnType + "]";
    }

}
