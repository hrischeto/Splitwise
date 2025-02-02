package mjtfinalproject.command.factory;

import mjtfinalproject.command.commands.badcommand.BadCommand;
import mjtfinalproject.command.Command;
import mjtfinalproject.command.commands.profilemanagement.LogIn;
import mjtfinalproject.command.commands.profilemanagement.Register;
import mjtfinalproject.entities.users.RegisteredUser;
import mjtfinalproject.repositories.Repository;
import mjtfinalproject.repositories.grouprepository.GroupRepository;
import mjtfinalproject.repositories.userrepository.UserRepository;

import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class CommandFactory {

    private static final String SEPARATOR = "\\s+";

    private static final String REGISTER = "register";
    private static final String LOGIN = "login";
    private static final String ADD_FRIEND = "add-friend";
    private static final String APPROVE_PAYMENT = "approve-payment";
    private static final String CONVERT_CURRENCY = "convert-currency";
    private static final String CREATE_GROUP = "create-group";
    private static final String GET_STATUS = "get-status";
    private static final String HELP = "help";
    private static final String HISTORY = "history";
    private static final String LOGOUT = "logout";
    private static final String MARK_AS_PAYED = "mark-as-payed";
    private static final String SPLIT = "split";
    private static final String SPLIT_GROUP = "split-group";
    private static final String STOP_SERVER = "stop-sever";

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    private final Map<SocketChannel, RegisteredUser> loggedUsers;

    public CommandFactory(GroupRepository groupRepository, UserRepository userRepository) {
        validateRepository(groupRepository);
        validateRepository(userRepository);

        this.groupRepository = groupRepository;
        this.userRepository = userRepository;

        loggedUsers = new ConcurrentHashMap<>();
    }

    public Command newCommand(String input, SocketChannel clientChanel) {
        List<String> tokens = Arrays.stream(input.split(SEPARATOR))
            .map(String::strip)
            .toList();

        String[] args = tokens.subList(1, tokens.size()).toArray(new String[0]);

        String commandTitle = tokens.getFirst();
        return switch (commandTitle) {
            case REGISTER -> new Register(userRepository, loggedUsers, clientChanel, args);
            case LOGIN -> new LogIn(userRepository, loggedUsers, clientChanel, args);
            default -> new BadCommand();
        };
    }

    void validateRepository(Repository repo) {
        if (Objects.isNull(repo)) {
            throw new IllegalArgumentException("Null repository.");
        }
    }
}
