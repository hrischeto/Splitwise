package mjt.project.exceptions;

public class InvalidGroupId extends RuntimeException {
    public InvalidGroupId(String message) {
        super(message);
    }

    public InvalidGroupId(String message, Throwable cause) {
        super(message, cause);
    }
}
