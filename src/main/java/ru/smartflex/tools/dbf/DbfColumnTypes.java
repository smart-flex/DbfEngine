package ru.smartflex.tools.dbf;

/**
 * Enumeration of the supported field types
 *
 * @author galisha
 * @since 1.00
 */
public enum DbfColumnTypes {
    Character("C"), Numeric("N"), Float("F"), Date("D"), Logical("L"), General(
            "G"), Memo("M");

    private String columnType;

    DbfColumnTypes(String columnType) {
        this.columnType = columnType;
    }

    protected String getColumnType() {
        return columnType;
    }

    /**
     * Returns dbf column type.
     *
     * @param type original dbf column type
     * @return dbf column type
     * @since 1.05
     */
    protected DbfColumnTypes getByOriginalType(String type) {
        DbfColumnTypes dct = null;

        for (DbfColumnTypes dc : DbfColumnTypes.values()) {
            if (dc.columnType.equals(type)) {
                dct = dc;
                break;
            }
        }

        return dct;
    }

}
