package mjtfinalproject.command.commands.relations;

import mjtfinalproject.entities.users.RegisteredUser;
import mjtfinalproject.repositories.grouprepository.GroupRepository;
import mjtfinalproject.repositories.userrepository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreateGroupTest {

    @Mock
    private RegisteredUser creatingUserMock;

    @Mock
    private GroupRepository groupRepositoryMock;
    @Mock
    private UserRepository userRepositoryMock;

    private CreateGroup createGroup;
    private CreateGroup createGroupWithShortInput;

    @BeforeEach
    void setUp() {
        String[] input = new String[] {"name", "user1", "user2"};
        String[] shortInput = new String[] {"name"};

        createGroup = new CreateGroup(userRepositoryMock, groupRepositoryMock, creatingUserMock, input);
        createGroupWithShortInput =
            new CreateGroup(userRepositoryMock, groupRepositoryMock, creatingUserMock, shortInput);
    }

    @Test
    void testNotEnoughArgumentsInInput() {
        assertTrue(createGroupWithShortInput.execute().contains("\"status\":\"ERROR\""),
            "When user input does not have enough arguments, a negative message is returned.");
    }

    @Test
    void testAllValidUsers() {
        RegisteredUser user1 = mock(RegisteredUser.class);
        RegisteredUser user2 = mock(RegisteredUser.class);

        when(userRepositoryMock.getUser("user1")).thenReturn(Optional.of(user1));
        when(userRepositoryMock.getUser("user2")).thenReturn(Optional.of(user2));

        String result = createGroup.execute();

        assertTrue(result.contains("\"status\":\"OK\""),
            "When a group is created successfully, a positive message should be returned.");

        verify(creatingUserMock, times(1)).addGroup(any());
        verify(user2, times(1)).addGroup(any());
        verify(user1, times(1)).addGroup(any());
    }

    @Test
    void testAUserNotExisting() {
        when(userRepositoryMock.getUser("user1")).thenReturn(Optional.empty());

        assertTrue(createGroup.execute().contains("\"status\":\"ERROR\""),
            "When an unregistered user is passed, a negative message should be returned.");

        verify(creatingUserMock, times(0)).addGroup(any());
    }
}
