package net.luporpi.dbtools.utils.exceptions;

/**
 * FlywayException.
 */
public final class FlywayException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     * 
     * @param errorMessage
     * @param err
     */
    public FlywayException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
