package net.luporpi.dbtools.utils.exceptions;

public final class ToolsException extends Exception {
    private static final long serialVersionUID = 1L;

    public ToolsException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}