package mjtfinalproject.command.commands.factory;

import mjtfinalproject.command.commands.badcommand.BadCommand;
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
import mjtfinalproject.command.factory.Command;
import mjtfinalproject.command.factory.CommandFactory;
import mjtfinalproject.entities.users.RegisteredUser;
import mjtfinalproject.logmanager.LogManager;
import mjtfinalproject.repositories.grouprepository.GroupRepository;
import mjtfinalproject.repositories.userrepository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommandFactoryTest {

    @Mock
    private GroupRepository groupRepositoryMock;
    @Mock
    private UserRepository userRepository;
    @Mock
    private LogManager logManagerMock;

    private SocketChannel clientChannelMock;

    @InjectMocks
    private CommandFactory commandFactory;

    @BeforeEach
    void setUp() {
        clientChannelMock = mock(SocketChannel.class);
    }

    @Test
    void testNullInput() {
        assertThrows(IllegalArgumentException.class, () -> commandFactory.newCommand(null, null));
    }

    @Test
    void testRegisterCommand() {
        Command command = commandFactory.newCommand("register user pass", clientChannelMock);

        assertInstanceOf(Register.class, command, "Expected Register command");
    }

    @Test
    void testLoginCommand() {
        Command command = commandFactory.newCommand("login user pass", clientChannelMock);

        assertInstanceOf(LogIn.class, command, "Expected LogIn command");
    }

    @Test
    void testInvalidCommandReturnsBadCommand() {
        Command command = commandFactory.newCommand("invalid-command", clientChannelMock);
        assertInstanceOf(BadCommand.class, command, "Expected BadCommand for invalid input");
    }

    @Test
    void testCommandsWhenLogged() {
        when(logManagerMock.isUserLogged(clientChannelMock)).thenReturn(true);
        RegisteredUser user = mock(RegisteredUser.class);
        when(logManagerMock.getUser(clientChannelMock)).thenReturn(user);

        assertInstanceOf(AddFriend.class, commandFactory.newCommand("add-friend friendName", clientChannelMock),
            "Expected AddFriend command");
        assertInstanceOf(StopServer.class, commandFactory.newCommand("stop-server", clientChannelMock),
            "Expected StopServer command");
        assertInstanceOf(ApprovePayment.class,
            commandFactory.newCommand("approve-payment 10 friend", clientChannelMock),
            "Expected ApprovedPayment command");
        assertInstanceOf(ApproveGroupMemberPayment.class,
            commandFactory.newCommand("approve-group-member-payment 10 friend group", clientChannelMock),
            "Expected ApproveGroupMemberPayment command");
        assertInstanceOf(CreateGroup.class,
            commandFactory.newCommand("create-group name friend1 friend2", clientChannelMock),
            "Expected CreateGroup command");
        assertInstanceOf(GetStatus.class,
            commandFactory.newCommand("get-status", clientChannelMock),
            "Expected GetStatus command");
        assertInstanceOf(Split.class,
            commandFactory.newCommand("split 10 friend", clientChannelMock),
            "Expected Split command");
        assertInstanceOf(SplitGroup.class,
            commandFactory.newCommand("split-group 10 group", clientChannelMock),
            "Expected SplitGroup command");
    }

    @Test
    void testCommandsWhenNotLogged() {
        when(logManagerMock.isUserLogged(clientChannelMock)).thenReturn(false);

        assertInstanceOf(BadCommand.class, commandFactory.newCommand("add-friend friendName", clientChannelMock),
            "Expected BadCommand, when user is not logged");
        assertInstanceOf(BadCommand.class, commandFactory.newCommand("stop-server", clientChannelMock),
            "Expected BadCommand, when user is not logged");
        assertInstanceOf(BadCommand.class,
            commandFactory.newCommand("approve-payment 10 friend", clientChannelMock),
            "Expected BadCommand, when user is not logged");
        assertInstanceOf(BadCommand.class,
            commandFactory.newCommand("approve-group-member-payment 10 friend group", clientChannelMock),
            "Expected BadCommand, when user is not logged");
        assertInstanceOf(BadCommand.class,
            commandFactory.newCommand("create-group name friend1 friend2", clientChannelMock),
            "Expected BadCommand, when user is not logged");
        assertInstanceOf(BadCommand.class,
            commandFactory.newCommand("get-status", clientChannelMock),
            "Expected BadCommand, when user is not logged");
        assertInstanceOf(BadCommand.class,
            commandFactory.newCommand("split 10 friend", clientChannelMock),
            "Expected BadCommand, when user is not logged");
        assertInstanceOf(BadCommand.class,
            commandFactory.newCommand("split-group 10 group", clientChannelMock),
            "Expected BadCommand, when user is not logged");
    }

}

