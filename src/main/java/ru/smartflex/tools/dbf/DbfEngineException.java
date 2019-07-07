package ru.smartflex.tools.dbf;

/**
 * Common exception class.
 *
 * @author galisha
 * @since 1.00
 */
@SuppressWarnings("serial")
public class DbfEngineException extends RuntimeException {

    public DbfEngineException() {
        super();
    }

    public DbfEngineException(String msg) {
        super(msg);
    }

    public DbfEngineException(String string, Throwable root) {
        super(string, root);
    }
}
