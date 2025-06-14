package mjt.project.command.commands.relations;

import mjt.project.command.CommandMessages;
import mjt.project.entities.users.RegisteredUser;
import mjt.project.repositories.grouprepository.GroupRepository;
import mjt.project.repositories.userrepository.UserRepository;
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

    @BeforeEach
    void setUp() {
        String[] input = new String[] {"name", "user1", "user2"};

        createGroup = new CreateGroup(userRepositoryMock, groupRepositoryMock, creatingUserMock, input);
    }

    @Test
    void testNotEnoughArgumentsInInput() {
        String[] shortInput = new String[] {"name"};
        CreateGroup createGroupWithShortInput =
            new CreateGroup(userRepositoryMock, groupRepositoryMock, creatingUserMock, shortInput);

        assertTrue(createGroupWithShortInput.execute().contains(CommandMessages.ERROR_MESSAGE.toString()),
            "When user input does not have enough arguments, a negative message is returned.");
    }

    @Test
    void testAllValidArguments() {
        RegisteredUser user1 = mock(RegisteredUser.class);
        RegisteredUser user2 = mock(RegisteredUser.class);

        when(user1.getUsername()).thenReturn("user1");
        when(user2.getUsername()).thenReturn("user2");
        when(creatingUserMock.getUsername()).thenReturn("creatingUser");

        when(userRepositoryMock.getUser("user1")).thenReturn(Optional.of(user1));
        when(userRepositoryMock.getUser("user2")).thenReturn(Optional.of(user2));

        when(user1.isGroupNameUnique("name")).thenReturn(true);
        when(creatingUserMock.isGroupNameUnique("name")).thenReturn(true);
        when(user2.isGroupNameUnique("name")).thenReturn(true);

        String result = createGroup.execute();

        assertTrue(result.contains(CommandMessages.OK_MESSAGE.toString()),
            "When a group is created successfully, a positive message should be returned.");

        verify(creatingUserMock, times(1)).addGroup(any());
        verify(user2, times(1)).addGroup(any());
        verify(user1, times(1)).addGroup(any());
    }

    @Test
    void testAUserNotExisting() {
        when(userRepositoryMock.getUser("user1")).thenReturn(Optional.empty());

        assertTrue(createGroup.execute().contains(CommandMessages.ERROR_MESSAGE.toString()),
            "When an unregistered user is passed, a negative message should be returned.");

        verify(creatingUserMock, times(0)).addGroup(any());
    }

    @Test
    void testNotUniqueGroupNameForUser() {
        RegisteredUser user1 = mock(RegisteredUser.class);
        when(userRepositoryMock.getUser("user1")).thenReturn(Optional.of(user1));

        when(user1.isGroupNameUnique("name")).thenReturn(false);

        String result = createGroup.execute();

        assertTrue(result.contains(CommandMessages.ERROR_MESSAGE.toString()),
            "When a group is not unique for any user, a negative message should be returned.");

        verify(creatingUserMock, times(0)).addGroup(any());
        verify(user1, times(0)).addGroup(any());
    }

    @Test
    void testNotUniqueGroupNameForCreatingUser() {
        RegisteredUser user1 = mock(RegisteredUser.class);
        when(userRepositoryMock.getUser("user1")).thenReturn(Optional.of(user1));

        RegisteredUser user2 = mock(RegisteredUser.class);
        when(userRepositoryMock.getUser("user2")).thenReturn(Optional.of(user2));
        when(user1.isGroupNameUnique("name")).thenReturn(true);
        when(user2.isGroupNameUnique("name")).thenReturn(true);
        when(creatingUserMock.isGroupNameUnique("name")).thenReturn(false);

        String result = createGroup.execute();

        assertTrue(result.contains(CommandMessages.ERROR_MESSAGE.toString()),
            "When a group is not unique for any user, a negative message should be returned.");

        verify(creatingUserMock, times(0)).addGroup(any());
        verify(user1, times(0)).addGroup(any());
        verify(user2, times(0)).addGroup(any());
    }
}
