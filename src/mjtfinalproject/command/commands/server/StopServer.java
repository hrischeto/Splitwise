package mjtfinalproject.command.commands.server;

import mjtfinalproject.command.factory.Command;
import mjtfinalproject.command.CommandMessages;
import mjtfinalproject.entities.users.RegisteredUser;
import mjtfinalproject.exceptions.FailedCommandCreationException;

import java.util.Objects;

public class StopServer implements Command {

    private static final String ADMIN_USERNAME = System.getenv("SplitwiseAdminUsername");
    private static final String STOP_COMMAND = "stop";

    private final RegisteredUser user;

    public StopServer(RegisteredUser user) {
        if (Objects.isNull(user)) {
            throw new FailedCommandCreationException("User was null");
        }

        this.user = user;
    }

    @Override
    public String execute() {
        if (isUserAdmin()) {
            return STOP_COMMAND;
        }

        return CommandMessages.ERROR_MESSAGE + " \"message\":\"You need administrator rights for this action.\"";
    }

    private boolean isUserAdmin() {
        return user.getUsername().equals(ADMIN_USERNAME);
    }
}
