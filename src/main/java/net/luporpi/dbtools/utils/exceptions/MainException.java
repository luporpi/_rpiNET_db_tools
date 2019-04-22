package net.luporpi.dbtools.utils.exceptions;

/**
 * MainException.
 */
public final class MainException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     * 
     * @param errorMessage
     * @param err
     */
    public MainException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
