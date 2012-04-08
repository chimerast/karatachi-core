package org.karatachi.expression;

public class UnknownIdentifierException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UnknownIdentifierException() {
        super();
    }

    public UnknownIdentifierException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownIdentifierException(String message) {
        super(message);
    }

    public UnknownIdentifierException(Throwable cause) {
        super(cause);
    }
}
