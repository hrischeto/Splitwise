package mjtfinalproject.command.commands.profilemanagement;

import mjtfinalproject.command.Command;
import mjtfinalproject.command.CommandMessages;
import mjtfinalproject.command.commands.profilemanagement.passwordencryptor.PasswordEncryptor;
import mjtfinalproject.entities.users.RegisteredUser;
import mjtfinalproject.exceptions.FailedCommandCreationException;
import mjtfinalproject.logmanager.LogManager;
import mjtfinalproject.repositories.userrepository.UserRepository;

import java.nio.channels.SocketChannel;
import java.util.Objects;
import java.util.Optional;

public class LogIn implements Command {

    protected static final int INPUT_LENGTH = 2;

    protected final String username;
    protected final String password;

    protected final UserRepository userRepository;

    protected final LogManager logManager;
    protected final SocketChannel clientChannel;

    public LogIn(UserRepository userRepository, LogManager logManager,
                 SocketChannel clientChannel, String... input) {
        validateInput(userRepository, logManager, clientChannel, input);

        this.userRepository = userRepository;
        this.logManager = logManager;
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
            return CommandMessages.ERROR_MESSAGE + " \"message\":\"Invalid input for \"login\" command.";
        }

        Optional<RegisteredUser> user = userRepository.getUser(username);
        if (user.isPresent()) {
            if (user.get().getPassword() == PasswordEncryptor.encryptPassword(password)) {
                logManager.logUser(clientChannel, user.get());
                
                return CommandMessages.OK_MESSAGE + " \"message\":\"User logged in successfully!\"";
            }

            return  CommandMessages.ERROR_MESSAGE + " \"message\":\"Wrong password!\"";
        }

        return CommandMessages.ERROR_MESSAGE + " \"message\":\"User with such username does not exists." +
            " Try again with a different username!\"";
    }

    private void validateInput(UserRepository userRepository, LogManager logManager,
                       SocketChannel clientChannel, String... input) {
        if (Objects.isNull(userRepository)) {
            throw new FailedCommandCreationException("User repository was null.");
        }

        if (Objects.isNull(logManager)) {
            throw new FailedCommandCreationException("Log manager was null.");
        }

        if (Objects.isNull(clientChannel)) {
            throw new FailedCommandCreationException("Client channel was null.");
        }

        if (Objects.isNull(input)) {
            throw new FailedCommandCreationException("User input for \"register\" command was null");
        }
    }

}
