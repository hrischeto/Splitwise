package mjt.project.exceptions;

public class InvalidObligationException extends RuntimeException {
    public InvalidObligationException(String message) {
        super(message);
    }

    public InvalidObligationException(String message, Throwable cause) {
        super(message, cause);
    }
}
