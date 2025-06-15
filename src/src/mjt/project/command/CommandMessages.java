package mjt.project.command;

public enum CommandMessages {
    ERROR_MESSAGE("\"status\":\"ERROR\""),
    OK_MESSAGE("\"status\":\"OK\"");

    private final String message;

    CommandMessages(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return  message;
    }
}
