package mjtfinalproject.command.commands.info;

import mjtfinalproject.entities.users.RegisteredUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetStatusTest {
    @Mock
    RegisteredUser userMock;

    @InjectMocks
    private GetStatus getStatus;

    @Test
    void testInvokeGetStatus() {
        when(userMock.getStatus()).thenReturn("some text");
        assertEquals(getStatus.execute(), "some text", "Status should coincide with user status.");

        verify(userMock, times(1)).getStatus();
    }
}
