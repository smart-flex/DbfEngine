package ru.smartflex.tools.dbf.mem;

/**
 * Contains literals and special constants.
 *
 * @author galisha
 * @since 1.05
 */
public interface MemConstants {

    int LENGTH_NUMERIC = 8;
    int LENGTH_DATE = 8;
    int LENGTH_LOGICAL = 1;
    int LENGTH_ARRAY_DIM = 4;

    byte MEM_FILE_EOF_BYTE = 0x1A;

    String EXCP_IO_ERROR = "IO error with mem";
    String EXCP_EOF = "Unexpected end of data";
    String EXCP_LN_POS = "Length must be positive";
    String EXCP_FILE_PROBLEM = "Some file error";
    String EXCP_INDEX_OUT_OF_BOUND = "Index array request is more than array size: ";
    String EXCP_MISS_MEM_NAME = "Memory name is missed";
    String EXCP_NF_VALUE_MEM_NAME = "Not found value for that memory name: ";
    String EXCP_STRING_CREATE = "Some error with string creation";
    String EXCP_ARRAY_OVER_ROW = "Row index exceeded the maximum allowable value: ";
    String EXCP_ARRAY_OVER_COL = "Col index exceeded the maximum allowable value: ";
    String EXCP_ARRAY_ROW_BAD = "Row index must be positive";
    String EXCP_ARRAY_COL_BAD = "Col index must be positive";
}
