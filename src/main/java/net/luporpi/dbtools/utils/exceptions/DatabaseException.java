package net.luporpi.dbtools.utils.exceptions;

public final class DatabaseException extends Exception {
    private static final long serialVersionUID = 1L;

    public DatabaseException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}