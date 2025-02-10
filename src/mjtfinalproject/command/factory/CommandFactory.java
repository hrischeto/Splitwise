package mjtfinalproject.command.factory;

import mjtfinalproject.command.commands.badcommand.BadCommand;
import mjtfinalproject.command.Command;
import mjtfinalproject.command.commands.info.GetStatus;
import mjtfinalproject.command.commands.paymentmanagement.ApproveGroupMemberPayment;
import mjtfinalproject.command.commands.paymentmanagement.ApprovePayment;
import mjtfinalproject.command.commands.profilemanagement.LogIn;
import mjtfinalproject.command.commands.profilemanagement.Register;
import mjtfinalproject.command.commands.relations.AddFriend;
import mjtfinalproject.command.commands.relations.CreateGroup;
import mjtfinalproject.command.commands.server.StopServer;
import mjtfinalproject.command.commands.split.Split;
import mjtfinalproject.command.commands.split.SplitGroup;
import mjtfinalproject.logmanager.LogManager;
import mjtfinalproject.logmanager.ParallelLogManager;
import mjtfinalproject.repositories.Repository;
import mjtfinalproject.repositories.grouprepository.GroupRepository;
import mjtfinalproject.repositories.userrepository.UserRepository;

import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CommandFactory {

    private static final String SEPARATOR = "\\s+";

    private static final String REGISTER = "register";
    private static final String LOGIN = "login";
    private static final String ADD_FRIEND = "add-friend";
    private static final String APPROVE_PAYMENT = "approve-payment";
    private static final String APPROVE_PAYMENT_GROUP = "approve-group-member-payment";
    private static final String CONVERT_CURRENCY = "convert-currency";
    private static final String CREATE_GROUP = "create-group";
    private static final String GET_STATUS = "get-status";
    private static final String HELP = "help";
    private static final String HISTORY = "history";
    private static final String LOGOUT = "logout";
    private static final String SPLIT = "split";
    private static final String SPLIT_GROUP = "split-group";
    private static final String STOP_SERVER = "stop-sever";

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    private final LogManager logManager;

    public CommandFactory(GroupRepository groupRepository, UserRepository userRepository) {
        validateRepository(groupRepository);
        validateRepository(userRepository);

        this.groupRepository = groupRepository;
        this.userRepository = userRepository;

        logManager = new ParallelLogManager();
    }

    public Command newCommand(String input, SocketChannel clientChannel) {
        List<String> tokens = Arrays.stream(input.split(SEPARATOR))
            .map(String::strip)
            .toList();

        String[] args = tokens.subList(1, tokens.size()).toArray(new String[0]);
        String commandTitle = tokens.getFirst();

        return switch (commandTitle) {
            case REGISTER, LOGIN -> profileCommand(commandTitle, args, clientChannel);
            case ADD_FRIEND, CREATE_GROUP -> relationsCommand(commandTitle, args, clientChannel);
            case STOP_SERVER ->
                logManager.isUserLogged(clientChannel) ? new StopServer(logManager.getUser(clientChannel)) :
                    new BadCommand();
            case SPLIT, SPLIT_GROUP -> splitCommand(commandTitle, args, clientChannel);
            case APPROVE_PAYMENT, APPROVE_PAYMENT_GROUP -> approveCommand(commandTitle, args, clientChannel);
            case GET_STATUS -> logManager.isUserLogged(clientChannel) ?
                new GetStatus(logManager.getUser(clientChannel)) : new BadCommand();
            default -> new BadCommand();
        };
    }

    void validateRepository(Repository repo) {
        if (Objects.isNull(repo)) {
            throw new IllegalArgumentException("Null repository.");
        }
    }

    private Command approveCommand(String commandTitle, String[] args, SocketChannel clientChannel) {
        return switch (commandTitle) {
            case APPROVE_PAYMENT -> logManager.isUserLogged(clientChannel) ?
                new ApprovePayment(logManager.getUser(clientChannel), userRepository, args) : new BadCommand();
            case APPROVE_PAYMENT_GROUP -> logManager.isUserLogged(clientChannel) ?
                new ApproveGroupMemberPayment(logManager.getUser(clientChannel), groupRepository, userRepository,
                    args) :
                new BadCommand();
            default -> new BadCommand();
        };
    }

    private Command profileCommand(String commandTitle, String[] args, SocketChannel clientChannel) {
        return switch (commandTitle) {
            case REGISTER -> new Register(userRepository, logManager, clientChannel, args);
            case LOGIN -> new LogIn(userRepository, logManager, clientChannel, args);
            default -> new BadCommand();
        };
    }

    private Command relationsCommand(String commandTitle, String[] args, SocketChannel clientChannel) {
        return switch (commandTitle) {
            case CREATE_GROUP -> logManager.isUserLogged(clientChannel) ?
                new CreateGroup(userRepository, groupRepository, logManager.getUser(clientChannel), args) :
                new BadCommand();
            case ADD_FRIEND -> logManager.isUserLogged(clientChannel) ?
                new AddFriend(logManager.getUser(clientChannel), userRepository, args) : new BadCommand();
            default -> new BadCommand();
        };
    }

    private Command splitCommand(String commandTitle, String[] args, SocketChannel clientChannel) {
        return switch (commandTitle) {
            case SPLIT -> logManager.isUserLogged(clientChannel) ?
                new Split(logManager.getUser(clientChannel), userRepository, args) : new BadCommand();
            case SPLIT_GROUP -> logManager.isUserLogged(clientChannel) ?
                new SplitGroup(groupRepository, userRepository, logManager.getUser(clientChannel), args) :
                new BadCommand();
            default -> new BadCommand();
        };
    }
}
