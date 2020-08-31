package ru.smartflex.tools.dbf;

/**
 * Contains literals and special constants.
 *
 * @author galisha
 * @since 1.00
 */
public interface DbfConstants {

    String DBF_ENGINE_VERSION = "1.09";

    String DEFAULT_CODE_PAGE = "Cp866";

    String EXCP_CODE_PAGE = "Code page is unknown";
    String EXCP_IO_ERROR = "IO error with dbf";
    String EXCP_HEADER_INF = "No information about dbf file header";
    String EXCP_HEADER_END = "Unexpected end of file by header";
    String EXCP_COLUMN_NOEXISTS = "Field is not existed: ";
    String EXCP_COLUMN_COL_TYPE = "There type mismatch for field: ";
    String EXCP_COLUMN_CP = "Error with supported code page";
    String EXCP_COLUMN_NUM = "Error with numeric format";
    String EXCP_HEADER_LEN = "DBF length record is not defined";
    String EXCP_REC_UNEXP = "Unexpected end of file by content. Was read records: ";
    String EXCP_COLUMN_DT = "Error with date format";
    String EXCP_DBF_EXISTS = "File is existed already";
    String EXCP_DBF_ERR_CREATE = "Error with file creating";
    String EXCP_CURR_REC_INFO = " on record: ";
    String EXCP_DBF_NOT_EXISTS = "File is not existed";
    String EXCP_DEF_COLS_NOT_ALLOWED = "Define columns is not allowed for append mode of existed file";

    String EXCP_COLUMN_ADD = "There were no column added";
    String EXCP_COLUMN_EMPTY = "Column has to be named";
    String EXCP_COLUMN_EXISTED = "Column already existed: ";
    String EXCP_COLUMN_NAMELEN = "Column length exceed 10 symbols";
    String EXCP_COLUMN_NOTYPE = "Column must have type";
    String EXCP_COLUMN_NODEC = "Column must not have dec value";
    String EXCP_COLUMN_CHARLEN = "Column must be between 1 and 254 symbols";
    String EXCP_COLUMN_NUMLEN = "Column must be between 1 and 20 digit";
    String EXCP_COLUMN_NUMDECLEN = "Column must have normal dec value";
    String EXCP_COLUMN_VAL_TOO_BIG = "Value too big for column: ";
    String EXCP_CP_MISSED = "There must be setup code page info";
    String EXCP_CP_ARITHERR = "There is ArithmeticException";

    int DBF_HEADER_LENGTH = 32;
    int DBF_COLUMN_LENGTH = 32;
    int DBF_COLUMN_NAME_LENGTH = 10;
    int DBF_COLUMN_CHAR_MAX_LEN = 254;
    int DBF_COLUMN_NUM_MAX_LEN = 20;
    int DBF_END_HEADER = 0x0D;
    int DBF_REC_FILLSYMB = 0x20;
    int DBF_END_OF_FILE = 0x1A;

    byte LOGICAL_TRUE = 'T';
    byte LOGICAL_FALSE = 'F';
    byte DELETED_MARKER = '*';

}
