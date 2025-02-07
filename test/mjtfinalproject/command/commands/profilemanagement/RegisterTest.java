package mjtfinalproject.command.commands.profilemanagement;

import mjtfinalproject.command.CommandMessages;
import mjtfinalproject.entities.users.RegisteredUser;
import mjtfinalproject.logmanager.LogManager;
import mjtfinalproject.repositories.userrepository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.channels.SocketChannel;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RegisterTest {

    @Mock
    private UserRepository userRepositoryMock;
    @Mock
    private LogManager logManagerMock;
    @Mock
    private SocketChannel clientChannelMock;

    private Register register;
    private Register registerWrongInput;

    @BeforeEach
    void setUp() {
        String[] input = new String[] {"username", "password"};
        String[] wrongInput = new String[] {"username", "password", "something"};

        register = new Register(userRepositoryMock, logManagerMock, clientChannelMock, input);
        registerWrongInput = new Register(userRepositoryMock, logManagerMock, clientChannelMock, wrongInput);
    }

    @Test
    void testWrongNumberOfArguments() {
        assertTrue(registerWrongInput.execute().contains(CommandMessages.ERROR_MESSAGE.toString()),
            "\"When user input has wrong input length, a negative message is returned.\"");
    }

    @Test
    void testNonexistentUsername() {
        when(userRepositoryMock.getUser(register.username)).thenReturn(Optional.empty());

        String result = register.execute();

        assertTrue(result.contains(CommandMessages.OK_MESSAGE.toString()),
            "When successfully registering a user, a positive message should be returned.");

        verify(logManagerMock, times(1)).logUser(any(), any());
        verify(userRepositoryMock, times(1)).addUser(any(RegisteredUser.class));
    }

    @Test
    void testExistingUsername() {
        RegisteredUser userMock = mock(RegisteredUser.class);
        Optional<RegisteredUser> optionalUser = Optional.of(userMock);

        when(userRepositoryMock.getUser(register.username)).thenReturn(optionalUser);

        assertTrue(register.execute().contains(CommandMessages.ERROR_MESSAGE.toString()),
            "When trying to register with already used name, a negative message should be returned.");

        verify(logManagerMock, times(0)).logUser(any(), any());
    }

}
