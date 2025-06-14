package mjt.project.command.commands.badcommand;

import mjt.project.command.factory.Command;

public class BadCommand implements Command {

    static final String BAD_COMMAND_MESSAGE = "\"status\":\"ERROR\", message: Invalid command string.";

    @Override
    public String execute() {
        return BAD_COMMAND_MESSAGE;
    }
}
