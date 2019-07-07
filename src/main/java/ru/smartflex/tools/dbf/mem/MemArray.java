package ru.smartflex.tools.dbf.mem;

import java.util.Date;

/**
 * Object container for xBase (Foxpro and Clipper) memory arrays
 *
 * @author galisha
 * @since 1.05
 */
class MemArray {

    private int rows;
    private int cols;
    private int size;
    private int index = 0;

    private Object[] objArray;

    MemArray(int rows, int cols) {
        super();
        this.rows = rows;
        this.cols = cols;

        size = rows;
        if (cols > 0) {
            size = rows * cols;
        }
        objArray = new Object[size];
    }

    private void checkPossibleToSave() {
        if (index >= size)
            throw new MemEngineException(MemConstants.EXCP_INDEX_OUT_OF_BOUND
                    + String.valueOf(size));
    }

    Object getElement(int row, int col) {
        if (cols > 0 && col > cols) {
            throw new MemEngineException(MemConstants.EXCP_ARRAY_OVER_COL
                    + String.valueOf(rows));
        }
        if (row > rows) {
            throw new MemEngineException(MemConstants.EXCP_ARRAY_OVER_ROW
                    + String.valueOf(rows));
        }
        if (cols > 0) {
            if (col <= 0) {
                throw new MemEngineException(MemConstants.EXCP_ARRAY_COL_BAD);
            }
        }
        if (row <= 0) {
            throw new MemEngineException(MemConstants.EXCP_ARRAY_ROW_BAD);
        }

        int index;
        if (cols == 0) {
            index = row - 1;
        } else {
            index = ((row - 1) * 2 + col) - 1;
        }

        return objArray[index];
    }

    void addDate(Date d) {
        checkPossibleToSave();

        objArray[index++] = d;
    }

    void addString(String s) {
        checkPossibleToSave();

        objArray[index++] = s;
    }

    void addBoolean(boolean b) {
        checkPossibleToSave();

        if (b) {
            objArray[index++] = Boolean.TRUE;
        } else {
            objArray[index++] = Boolean.FALSE;
        }
    }

    void addDouble(double d) {
        checkPossibleToSave();

        objArray[index++] = Double.valueOf(d);
    }

    boolean isAbbleToAppend() {
        boolean fok = true;
        if (index >= size) {
            fok = false;
        }
        return fok;
    }
}
