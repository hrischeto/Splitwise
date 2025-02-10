package mjtfinalproject.command.commands.info;

import mjtfinalproject.command.factory.Command;
import mjtfinalproject.entities.users.RegisteredUser;
import mjtfinalproject.exceptions.FailedCommandCreationException;

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
