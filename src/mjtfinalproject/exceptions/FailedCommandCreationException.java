package mjtfinalproject.exceptions;

public class FailedCommandCreationException extends RuntimeException {

    public FailedCommandCreationException(String message) {
        super(message);
    }

    public FailedCommandCreationException(String message, Throwable cause) {
        super(message, cause);
    }

}
