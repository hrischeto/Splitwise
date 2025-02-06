package mjtfinalproject.command.commands.profilemanagement;

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

    private Map<SocketChannel, RegisteredUser> loggedUsers;
    @Mock
    private SocketChannel clientChannelMock;

    private Register register;
    private Register registerWrongInput;

    @BeforeEach
    void setUp() {
        String[] input = new String[] {"username", "password"};
        String[] wrongInput = new String[] {"username", "password", "something"};

        loggedUsers = new ConcurrentHashMap<>();
        register = new Register(userRepositoryMock, loggedUsers, clientChannelMock, input);
        registerWrongInput = new Register(userRepositoryMock, loggedUsers, clientChannelMock, wrongInput);
    }

    @Test
    void testWrongNumberOfArguments() {
        assertTrue(registerWrongInput.execute().contains("\"status\":\"ERROR\""),
            "\"When user input has wrong input length, a negative message is returned.\"");
    }

    @Test
    void testNonexistentUsername() {
        when(userRepositoryMock.getUser(register.username)).thenReturn(Optional.empty());

        String result = register.execute();

        assertTrue(result.contains("\"status\":\"OK\""),
            "When successfully registering a user, a positive message should be returned.");
        assertTrue(loggedUsers.containsKey(clientChannelMock),
            "When registering, the new user should be automatically logged in.");

        verify(userRepositoryMock, times(1)).addUser(any(RegisteredUser.class));
    }

    @Test
    void testExistingUsername() {
        RegisteredUser userMock = mock(RegisteredUser.class);
        Optional<RegisteredUser> optionalUser = Optional.of(userMock);

        when(userRepositoryMock.getUser(register.username)).thenReturn(optionalUser);

        assertTrue(register.execute().contains("\"status\":\"ERROR\""),
            "When trying to register with already used name, a negative message should be thrown.");
    }

}
