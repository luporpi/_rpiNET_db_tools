package net.luporpi.dbtools.utils.exceptions;

public final class FlywayException extends Exception {
    private static final long serialVersionUID = 1L;

    public FlywayException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}