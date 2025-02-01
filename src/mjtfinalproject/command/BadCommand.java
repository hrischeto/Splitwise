package mjtfinalproject.command;

public class BadCommand implements Command {

    static final String BAD_COMMAND_MESSAGE = "\"status\":\"ERROR\", message: Invalid command string.";

    @Override
    public String execute() {
        return BAD_COMMAND_MESSAGE;
    }
}
