package mjt.project.command.commands.info;

import mjt.project.command.factory.Command;
import mjt.project.entities.users.RegisteredUser;
import mjt.project.exceptions.FailedCommandCreationException;

import java.util.Objects;

public class GetStatus implements Command {

    private final RegisteredUser user;

    public GetStatus(RegisteredUser user) {
        if (Objects.isNull(user)) {
            throw new FailedCommandCreationException("Null user");
        }
        this.user = user;
    }

    @Override
    public String execute() {
        return user.getStatus();
    }
}
