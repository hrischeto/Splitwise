package mjtfinalproject.command.commands.relations;

import mjtfinalproject.command.factory.Command;
import mjtfinalproject.command.CommandMessages;
import mjtfinalproject.entities.users.RegisteredUser;
import mjtfinalproject.exceptions.FailedCommandCreationException;
import mjtfinalproject.exceptions.InvalidEntity;
import mjtfinalproject.repositories.userrepository.UserRepository;

import java.util.Objects;
import java.util.Optional;

public class AddFriend implements Command {

    private static final int INPUT_LENGTH = 1;

    private final RegisteredUser user;
    private final String friendToAdd;
    private final UserRepository userRepository;

    public AddFriend(RegisteredUser user, UserRepository userRepository, String... input) {
        validateArguments(user, userRepository, input);

        if (input.length != INPUT_LENGTH) {
            this.friendToAdd = null;
            this.user = null;
            this.userRepository = null;
        } else {
            this.user = user;
            this.friendToAdd = input[0];
            this.userRepository = userRepository;
        }
    }

    @Override
    public String execute() {
        if (Objects.isNull(friendToAdd)) {
            return CommandMessages.ERROR_MESSAGE + " \"message\":\"Invalid input for \"add-friend\" command.";
        }

        Optional<RegisteredUser> optionalFriend = userRepository.getUser(friendToAdd);
        if (optionalFriend.isEmpty()) {
            return CommandMessages.ERROR_MESSAGE + " \"message\":\"No such user found\"";
        }

        RegisteredUser friendUser = optionalFriend.get();
        try {
            user.addFriend(friendUser);
            friendUser.addFriend(user);
        } catch (InvalidEntity e) {
            return CommandMessages.ERROR_MESSAGE + " \"message\":" + e.getMessage();
        }

        return CommandMessages.OK_MESSAGE + " \"message\":\"You are friends with \"" + friendUser.getUsername() + "!";
    }

    private void validateArguments(RegisteredUser user, UserRepository userRepository, String... input) {
        if (Objects.isNull(user)) {
            throw new FailedCommandCreationException("User was null.");
        }

        if (Objects.isNull(userRepository)) {
            throw new FailedCommandCreationException("User repository was null.");
        }

        if (Objects.isNull(input)) {
            throw new FailedCommandCreationException("User input is null.");
        }
    }

    public String getFriendToAdd() {
        return friendToAdd;
    }
}
