package info.macias.sse.err;

import java.io.IOException;

/**
 * Exception thrown when trying to send a message over a completed (ended) asynchronous connection.
 */
public class ClosedConnectionException extends IOException {
    private static final String MSG = "connection already closed";
    public ClosedConnectionException() {
        super(MSG);
    }

    public ClosedConnectionException(String message) {
        super(message);
    }

    public ClosedConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClosedConnectionException(Throwable cause) {
        super(MSG, cause);
    }
}
