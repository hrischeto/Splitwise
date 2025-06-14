package mjt.project.exceptions;

public class FailedPasswordHashingException extends RuntimeException {
    public FailedPasswordHashingException(String message) {
        super(message);
    }

    public FailedPasswordHashingException(String message, Throwable cause) {
        super(message, cause);
    }
}