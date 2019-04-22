package net.luporpi.dbtools.utils.exceptions;

public final class MainException extends Exception {
    private static final long serialVersionUID = 1L;

    public MainException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}