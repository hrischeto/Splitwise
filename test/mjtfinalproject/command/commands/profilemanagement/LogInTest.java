package mjtfinalproject.command.commands.profilemanagement;

import mjtfinalproject.command.CommandMessages;
import mjtfinalproject.command.commands.profilemanagement.passwordencryptor.PasswordEncryptor;
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
public class LogInTest {

    @Mock
    private UserRepository userRepositoryMock;
    @Mock
    private LogManager logManagerMock;
    @Mock
    private SocketChannel clientChannelMock;

    private LogIn login;
    private LogIn loginWrongInputLength;

    @BeforeEach
    void setUp() {
        String[] input = new String[] {"username", "password"};
        String[] wrongInput = new String[] {"username", "password", "something"};

        login = new LogIn(userRepositoryMock, logManagerMock, clientChannelMock, input);
        loginWrongInputLength = new LogIn(userRepositoryMock, logManagerMock, clientChannelMock, wrongInput);
    }

    @Test
    void testWrongLengthInput() {
        assertTrue(loginWrongInputLength.execute().contains(CommandMessages.ERROR_MESSAGE.toString()),
            "When user input has wrong input length, a negative message is returned.");
    }

    @Test
    void testCorrectNameCorrectPassword() {
        RegisteredUser userMock = mock(RegisteredUser.class);
        when(userMock.getPassword()).thenReturn(PasswordEncryptor.encryptPassword(login.password));

        Optional<RegisteredUser> optionalUser = Optional.of(userMock);

        when(userRepositoryMock.getUser(login.username)).thenReturn(optionalUser);

        String result = login.execute();

        assertTrue(result.contains(CommandMessages.OK_MESSAGE.toString()),
            "When successfully logging in a user, a positive message should be returned.");

        verify(logManagerMock, times(1)).logUser(clientChannelMock, optionalUser.get());
        verify(userMock, times(1)).getNewNotifications();
    }

    @Test
    void testNonexistentName() {
        when(userRepositoryMock.getUser(login.username)).thenReturn(Optional.empty());

        String result = login.execute();

        assertTrue(result.contains(CommandMessages.ERROR_MESSAGE.toString()),
            "When a not registered user tries to log in a negative message should be returned.");

        verify(logManagerMock, times(0)).logUser(any(), any());
    }

    @Test
    void testCorrectNameWrongPassword() {
        RegisteredUser userMock = mock(RegisteredUser.class);
        when(userMock.getPassword()).thenReturn("differentPassword".hashCode());

        Optional<RegisteredUser> optionalUser = Optional.of(userMock);
        when(userRepositoryMock.getUser(login.username)).thenReturn(optionalUser);

        String result = login.execute();

        assertTrue(result.contains(CommandMessages.ERROR_MESSAGE.toString()),
            "When finding a registered user but entering a wrong password for it, a negative message should be returned.");

        verify(logManagerMock, times(0)).logUser(any(), any());
    }

}
