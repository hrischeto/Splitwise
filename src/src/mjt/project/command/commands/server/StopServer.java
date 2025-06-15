package mjt.project.command.commands.server;

import mjt.project.command.factory.Command;
import mjt.project.command.CommandMessages;
import mjt.project.entities.users.RegisteredUser;
import mjt.project.exceptions.FailedCommandCreationException;

import java.util.Objects;

public class StopServer implements Command {

    public static final String STOP_COMMAND = "stop";
    private final String adminUsername;

    private final RegisteredUser user;

    public StopServer(RegisteredUser user) {
        if (Objects.isNull(user)) {
            throw new FailedCommandCreationException("User was null");
        }

        adminUsername = System.getenv("SplitwiseAdminUsername");
        this.user = user;
    }

    StopServer(RegisteredUser user, String adminUsername) {
        if (Objects.isNull(user)) {
            throw new FailedCommandCreationException("User was null");
        }
        if (Objects.isNull(adminUsername)) {
            throw new FailedCommandCreationException("Username was null");
        }

        this.adminUsername = adminUsername;
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
        return user.getUsername().equals(adminUsername);
    }
}
