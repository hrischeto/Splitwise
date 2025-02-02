package mjtfinalproject.command.commands.profilemanagement;

import mjtfinalproject.command.Command;
import mjtfinalproject.entities.users.RegisteredUser;
import mjtfinalproject.exceptions.FailedCommandCreationException;
import mjtfinalproject.repositories.userrepository.UserRepository;

import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Objects;

public class Register implements Command {

    private static final int INPUT_LENGTH = 2;
    private final String username;
    private final String password;

    private final UserRepository userRepository;

    private final Map<SocketChannel, RegisteredUser> loggedUsers;
    private final SocketChannel clientChannel;

    public Register(UserRepository userRepository, Map<SocketChannel, RegisteredUser> loggedUsers,
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
        if (username.isEmpty() || password.isEmpty()) {
            return "\"status\":\"ERROR\", \"message\":\"Invalid input for \"register\" command.";
        }

        if (userRepository.getUser(username).isEmpty()) {
            RegisteredUser user = new RegisteredUser(username, password);
            userRepository.addUser(user);

            loggedUsers.put(clientChannel, user);

            return "\"status\":\"OK\", \"message\":\"User registered and logged successfully!\"";
        }

        return "\"status\":\"ERROR\", \"message\":\"User with such username already exists." +
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
