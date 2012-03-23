package org.karatachi.exception;

public class IncompatiblePlatformException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public IncompatiblePlatformException() {
        super();
    }

    public IncompatiblePlatformException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncompatiblePlatformException(String message) {
        super(message);
    }

    public IncompatiblePlatformException(Throwable cause) {
        super(cause);
    }
}
