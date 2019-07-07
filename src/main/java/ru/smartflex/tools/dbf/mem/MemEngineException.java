package ru.smartflex.tools.dbf.mem;

/**
 * Common mem exception class.
 *
 * @author galisha
 * @since 1.05
 */
public class MemEngineException extends RuntimeException {

    private static final long serialVersionUID = 6692821756814731828L;

    public MemEngineException() {
        super();
    }

    public MemEngineException(String msg) {
        super(msg);
    }

    public MemEngineException(String string, Throwable root) {
        super(string, root);
    }

}
