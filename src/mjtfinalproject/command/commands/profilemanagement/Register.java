package mjtfinalproject.command.commands.profilemanagement;

import mjtfinalproject.entities.users.RegisteredUser;
import mjtfinalproject.repositories.userrepository.UserRepository;

import java.nio.channels.SocketChannel;
import java.util.Map;

public class Register extends LogIn {

    public Register(UserRepository userRepository, Map<SocketChannel, RegisteredUser> loggedUsers,
                    SocketChannel clientChannel, String... input) {
        super(userRepository, loggedUsers, clientChannel, input);
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

}
