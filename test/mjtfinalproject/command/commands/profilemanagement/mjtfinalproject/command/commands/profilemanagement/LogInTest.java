package mjtfinalproject.command.commands.profilemanagement;

import mjtfinalproject.command.commands.profilemanagement.passwordencryptor.PasswordEncryptor;
import mjtfinalproject.entities.users.RegisteredUser;
import mjtfinalproject.repositories.userrepository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LogInTest {

    private String[] input;

    @Mock
    private UserRepository userRepositoryMock;

    private Map<SocketChannel, RegisteredUser> loggedUsers;
    @Mock
    private SocketChannel clientChannelMock;

    private LogIn login;

    @BeforeEach
    void setUp(){
        input = new String[]{"username","password"};
        loggedUsers = new ConcurrentHashMap<>();
        login = new LogIn(userRepositoryMock, loggedUsers, clientChannelMock, input);
    }

    @Test
    void testCorrectNameCorrectPassword() {
        RegisteredUser userMock = mock(RegisteredUser.class);
        when(userMock.getPassword()).thenReturn(PasswordEncryptor.encryptPassword(login.password));

        Optional<RegisteredUser> optionalUser = Optional.of(userMock);

        when(userRepositoryMock.getUser(login.username)).thenReturn(optionalUser);

        String result = login.execute();

        assertTrue(result.contains("\"status\":\"OK\""),
            "When successfully logging in a user, a positive message should be returned.");
        assertTrue(loggedUsers.containsKey(clientChannelMock),
            "The user's channel should be added to the active channels.");
        assertEquals(loggedUsers.get(clientChannelMock), userMock, "The user should be associated with the channel they are using");
    }

    @Test
    void testNonexistentNameCorrectPassword() {
        when(userRepositoryMock.getUser(login.username)).thenReturn(Optional.empty());

        String result = login.execute();

        assertTrue(result.contains("\"status\":\"ERROR\""),
            "When a not registered user tries to log in a negative message should be thrown.");
        assertFalse(loggedUsers.containsKey(clientChannelMock));
    }

    @Test
    void testCorrectNameWrongPassword() {
        RegisteredUser userMock = mock(RegisteredUser.class);
        when(userMock.getPassword()).thenReturn(PasswordEncryptor.encryptPassword("wrongPassword"));

        Optional<RegisteredUser> optionalUser = Optional.of(userMock);

        when(userRepositoryMock.getUser(login.username)).thenReturn(optionalUser);

        String result = login.execute();

        assertTrue(result.contains("\"status\":\"ERROR\""),
            "When finding a registered user but entering a wrong password for it a negative message should be thrown.");
        assertFalse(loggedUsers.containsKey(clientChannelMock));
    }

}
