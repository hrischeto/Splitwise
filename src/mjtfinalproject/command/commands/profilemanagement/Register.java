package mjtfinalproject.command.commands.profilemanagement;

import mjtfinalproject.command.CommandMessages;
import mjtfinalproject.entities.users.RegisteredUser;
import mjtfinalproject.entities.users.RegisteredUserImpl;
import mjtfinalproject.logmanager.LogManager;
import mjtfinalproject.repositories.userrepository.UserRepository;

import java.nio.channels.SocketChannel;
import java.util.Objects;

public class Register extends LogIn {

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
            RegisteredUser user = new RegisteredUserImpl(username, password);
            userRepository.addUser(user);

            logManager.logUser(clientChannel, user);

            return CommandMessages.OK_MESSAGE + " \"message\":\"User registered and logged successfully!\"\n" +
                user.getNewNotifications();
        }

        return CommandMessages.ERROR_MESSAGE + " \"message\":\"User with such username already exists." +
            " Try again with a different username!\"";
    }

}
