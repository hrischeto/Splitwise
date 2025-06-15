package mjt.project.command.factory;

import mjt.project.command.commands.badcommand.BadCommand;
import mjt.project.command.commands.currencyconversion.ConvertCurrency;
import mjt.project.command.commands.info.GetStatus;
import mjt.project.command.commands.paymentmanagement.ApproveGroupMemberPayment;
import mjt.project.command.commands.paymentmanagement.ApprovePayment;
import mjt.project.command.commands.profilemanagement.LogIn;
import mjt.project.command.commands.profilemanagement.Register;
import mjt.project.command.commands.relations.AddFriend;
import mjt.project.command.commands.relations.CreateGroup;
import mjt.project.command.commands.server.StopServer;
import mjt.project.command.commands.split.Split;
import mjt.project.command.commands.split.SplitGroup;
import mjt.project.currencymanagment.CurrencyManager;
import mjt.project.logmanager.LogManager;
import mjt.project.logmanager.ParallelLogManager;
import mjt.project.repositories.Repository;
import mjt.project.repositories.grouprepository.GroupRepository;
import mjt.project.repositories.userrepository.UserRepository;

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
    private static final String AVAILABLE_SYMBOLS = "get-currency-symbols";
    private static final String CREATE_GROUP = "create-group";
    private static final String GET_STATUS = "get-status";
    private static final String HELP = "help";
    private static final String HISTORY = "history";
    private static final String LOGOUT = "logout";
    private static final String SPLIT = "split";
    private static final String SPLIT_GROUP = "split-group";
    private static final String STOP_SERVER = "stop-server";

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    private final CurrencyManager currencyManager;
    private final LogManager logManager;

    public CommandFactory(GroupRepository groupRepository, UserRepository userRepository) {
        validateRepository(groupRepository);
        validateRepository(userRepository);

        this.groupRepository = groupRepository;
        this.userRepository = userRepository;

        logManager = new ParallelLogManager();
        currencyManager = new CurrencyManager();
    }

    public CommandFactory(GroupRepository groupRepository, UserRepository userRepository, LogManager logManager, CurrencyManager currencyManager) {
        validateRepository(groupRepository);
        validateRepository(userRepository);

        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.logManager = logManager;
        this.currencyManager = currencyManager;
    }

    public Command newCommand(String input, SocketChannel clientChannel) {
        validateCommandInput(input, clientChannel);

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
            case CONVERT_CURRENCY ->
                logManager.isUserLogged(clientChannel) ? new ConvertCurrency(logManager.getUser(clientChannel), currencyManager, args) :
                    new BadCommand();
            default -> new BadCommand();
        };
    }

    void validateRepository(Repository repo) {
        if (Objects.isNull(repo)) {
            throw new IllegalArgumentException("Null repository.");
        }
    }

    void validateCommandInput(String input, SocketChannel clientChannel) {
        if (Objects.isNull(input)) {
            throw new IllegalArgumentException("Null client input.");
        }
        if (Objects.isNull(clientChannel)) {
            throw new IllegalArgumentException("Null client channel.");
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
