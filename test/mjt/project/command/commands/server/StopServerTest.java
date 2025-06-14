package mjt.project.command.commands.server;

import mjt.project.command.CommandMessages;
import mjt.project.entities.users.RegisteredUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StopServerTest {

    @Mock
    private RegisteredUser userMock;

    @Test
    void testUserIsAdmin() {
        StopServer stopServer = new StopServer(userMock, "adminUsername");
        when(userMock.getUsername()).thenReturn("adminUsername");

        assertEquals("stop", stopServer.execute(),
            "When admin calls this command, a stop command should be returned.");
    }

    @Test
    void testUserNotAdmin() {
        StopServer stopServer = new StopServer(userMock);
        when(userMock.getUsername()).thenReturn("someName");

        assertTrue(stopServer.execute().contains(CommandMessages.ERROR_MESSAGE.toString()),
            "When not admin calls this command, a negative message should be returned.");
    }
}
