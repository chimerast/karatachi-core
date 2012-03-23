package org.karatachi.exception;

public class IncompatibleConfigurationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public IncompatibleConfigurationException() {
        super();
    }

    public IncompatibleConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncompatibleConfigurationException(String message) {
        super(message);
    }

    public IncompatibleConfigurationException(Throwable cause) {
        super(cause);
    }
}
