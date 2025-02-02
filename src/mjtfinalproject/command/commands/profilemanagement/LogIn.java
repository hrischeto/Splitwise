package mjtfinalproject.command.commands.profilemanagement;

import mjtfinalproject.command.Command;
import mjtfinalproject.entities.users.RegisteredUser;
import mjtfinalproject.exceptions.FailedCommandCreationException;
import mjtfinalproject.repositories.userrepository.UserRepository;

import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class LogIn implements Command {

    private static final int INPUT_LENGTH = 2;

    private final String username;
    private final String password;

    private final UserRepository userRepository;

    private final Map<SocketChannel, RegisteredUser> loggedUsers;
    private final SocketChannel clientChannel;

    public LogIn(UserRepository userRepository, Map<SocketChannel, RegisteredUser> loggedUsers,
                 SocketChannel clientChannel, String... input) {
        validateInput(userRepository, loggedUsers, clientChannel, input);

        this.userRepository = userRepository;
        this.loggedUsers = loggedUsers;
        this.clientChannel = clientChannel;

        if (input.length != INPUT_LENGTH) {
            username = null;
            password = null;
        } else {
            username = input[0];
            password = input[1];
        }
    }

    @Override
    public String execute() {
        if (Objects.isNull(username) || Objects.isNull(password)) {
            return "\"status\":\"ERROR\", \"message\":\"Invalid input for \"login\" command.";
        }

        Optional<RegisteredUser> user = userRepository.getUser(username);
        if (user.isPresent()) {
            loggedUsers.put(clientChannel, user.get());
            return "\"status\":\"OK\", \"message\":\"User logged in successfully!\"";
        }

        return "\"status\":\"ERROR\", \"message\":\"User with such username does not exists." +
            " Try again with a different username!\"";
    }

    void validateInput(UserRepository userRepository, Map<SocketChannel, RegisteredUser> loggedUsers,
                       SocketChannel clientChannel, String... input) {
        if (Objects.isNull(userRepository)) {
            throw new FailedCommandCreationException("User repository was null.");
        }

        if (Objects.isNull(loggedUsers)) {
            throw new FailedCommandCreationException("Logged users map was null.");
        }

        if (Objects.isNull(clientChannel)) {
            throw new FailedCommandCreationException("Client channel was null.");
        }

        if (Objects.isNull(input)) {
            throw new FailedCommandCreationException("User input for \"register\" command was null");
        }
    }
}
