package net.luporpi.dbtools.utils.exceptions;

/**
 * DatabaseException.
 */
public final class DatabaseException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     * 
     * @param errorMessage
     * @param err
     */
    public DatabaseException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
