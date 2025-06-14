package mjt.project.command.commands.profilemanagement;

import mjt.project.command.CommandMessages;
import mjt.project.entities.users.RegisteredUser;
import mjt.project.entities.users.RegisteredUserImpl;
import mjt.project.logmanager.LogManager;
import mjt.project.repositories.userrepository.UserRepository;

import java.nio.channels.SocketChannel;
import java.util.Objects;

public class Register extends LogIn {

    private static final String SECURE_PASSWORD = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).{8,}$\n";

    public Register(UserRepository userRepository, LogManager logManager,
                    SocketChannel clientChannel, String... input) {
        super(userRepository, logManager, clientChannel, input);
    }

    @Override
    public String execute() {
        if (Objects.isNull(username) || Objects.isNull(password)) {
            return CommandMessages.ERROR_MESSAGE + " \"message\":\"Invalid input for \"register\" command.";
        }

        if (userRepository.getUser(username).isEmpty()) {

           /* if (!password.equals(SECURE_PASSWORD)) {
                return CommandMessages.ERROR_MESSAGE + " \"message\":\"Choose a secure password.\"";
            }*/
            RegisteredUser user = new RegisteredUserImpl(username, password);
            userRepository.addUser(user);

            logManager.logUser(clientChannel, user);

            return CommandMessages.OK_MESSAGE + " \"message\":\"User registered and logged successfully!\"";
        }

        return CommandMessages.ERROR_MESSAGE + " \"message\":\"User with such username already exists." +
            " Try again with a different username!\"";
    }

}
