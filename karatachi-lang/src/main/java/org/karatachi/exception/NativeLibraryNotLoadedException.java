package org.karatachi.exception;

public class NativeLibraryNotLoadedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NativeLibraryNotLoadedException() {
        super();
    }

    public NativeLibraryNotLoadedException(String message, Throwable cause) {
        super(message, cause);
    }

    public NativeLibraryNotLoadedException(String message) {
        super(message);
    }

    public NativeLibraryNotLoadedException(Throwable cause) {
        super(cause);
    }
}
