package net.luporpi.dbtools.utils.exceptions;

/**
 * ToolsException.
 */
public final class ToolsException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     * 
     * @param errorMessage
     * @param err
     */
    public ToolsException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
