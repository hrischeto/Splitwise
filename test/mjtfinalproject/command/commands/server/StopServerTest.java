package mjtfinalproject.command.commands.server;

import mjtfinalproject.command.CommandMessages;
import mjtfinalproject.entities.users.RegisteredUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StopServerTest {

    @Mock
    private RegisteredUser userMock;

    @InjectMocks
    private StopServer stopServer;

    @Test
    void testUserIsAdmin() {
        when(userMock.getUsername()).thenReturn(System.getenv("SplitwiseAdminUsername"));

        assertEquals("stop", stopServer.execute(),
            "When admin calls this command, a stop command should be returned.");
    }

    @Test
    void testUserNotAdmin() {
        when(userMock.getUsername()).thenReturn("someName");

        assertTrue(stopServer.execute().contains(CommandMessages.ERROR_MESSAGE.toString()),
            "When not admin calls this command, a negative message should be returned.");
    }
}
