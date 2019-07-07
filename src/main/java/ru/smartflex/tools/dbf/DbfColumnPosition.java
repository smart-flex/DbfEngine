package ru.smartflex.tools.dbf;

/**
 * Contains position data for xBase field.
 *
 * @author galisha
 * @since 1.00
 */
public class DbfColumnPosition {
    private int offset = -1;
    private int columnLength;
    private int columnDotAmount;

    DbfColumnPosition(int offset, int columnLength,
                                int columnDotAmount) {
        super();
        this.offset = offset;
        this.columnLength = columnLength;
        this.columnDotAmount = columnDotAmount;
    }

    DbfColumnPosition(int columnLength, int columnDotAmount) {
        super();
        this.columnLength = columnLength;
        this.columnDotAmount = columnDotAmount;
    }

    int getOffset() {
        return offset;
    }

    void setOffset(int offset) {
        this.offset = offset;
    }

    int getColumnLength() {
        return columnLength;
    }

    int getColumnDotAmount() {
        return columnDotAmount;
    }

    /**
     * toString method
     *
     * @since 1.07
     */
    @Override
    public String toString() {
        return "DbfColumnPosition [offset=" + offset + ", columnLength="
                + columnLength + "]";
    }
}
