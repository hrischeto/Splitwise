package mjtfinalproject.logmanager;

import mjtfinalproject.entities.users.RegisteredUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.channels.SocketChannel;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParallelLogManagerTest {
    @Mock
    private Map<SocketChannel, RegisteredUser> loggedUsersMock;

    @InjectMocks
    private ParallelLogManager logManager;

    @Test
    void testThrowsWhenArgumentIsNull() {
        assertThrows(IllegalArgumentException.class, () -> logManager.isUserLogged(null),
            "When the client channel is null, an exception should be thrown.");
        assertThrows(IllegalArgumentException.class, () -> logManager.logUser(null, null),
            "When an argument is null, an exception should be thrown.");
        assertThrows(IllegalArgumentException.class, () -> logManager.getUser(null),
            "When the client channel is null, an exception should be thrown.");
        assertThrows(IllegalArgumentException.class, () -> logManager.logOutUser(null),
            "When the client channel is null, an exception should be thrown.");
    }

    @Test
    void testUserIsLogged() {
        SocketChannel channel = mock(SocketChannel.class);
        when(loggedUsersMock.containsKey(channel)).thenReturn(true);

        assertTrue(logManager.isUserLogged(channel), "When channel is present in the logged database, return true.");
    }

    @Test
    void testUserNotLogged() {
        SocketChannel channel = mock(SocketChannel.class);
        when(loggedUsersMock.containsKey(channel)).thenReturn(false);

        assertFalse(logManager.isUserLogged(channel),
            "When channel is not present in the logged database, return false.");
    }

    @Test
    void testLogging() {
        SocketChannel channel = mock(SocketChannel.class);
        RegisteredUser user = mock(RegisteredUser.class);

        logManager.logUser(channel, user);

        verify(loggedUsersMock, times(1)).put(channel, user);
    }

    @Test
    void testGetLoggedUser() {
        SocketChannel channel = mock(SocketChannel.class);
        RegisteredUser user = mock(RegisteredUser.class);
        when(loggedUsersMock.containsKey(channel)).thenReturn(true);
        when(loggedUsersMock.get(channel)).thenReturn(user);

        assertEquals(user, logManager.getUser(channel), "When user is logged it is returned successfully");
    }

    @Test
    void testGetNotLoggedUser() {
        SocketChannel channel = mock(SocketChannel.class);
        when(loggedUsersMock.containsKey(channel)).thenReturn(false);

        assertNull(logManager.getUser(channel), "When user is not logged it is returned null");
    }

    @Test
    void testLoggingOut() {
        SocketChannel channel = mock(SocketChannel.class);
        RegisteredUser user = mock(RegisteredUser.class);
        when(loggedUsersMock.containsKey(channel)).thenReturn(true);

        logManager.logOutUser(channel);

        verify(loggedUsersMock, times(1)).remove(channel);
    }

}
