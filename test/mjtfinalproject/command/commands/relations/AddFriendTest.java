package mjtfinalproject.command.commands.relations;

import mjtfinalproject.entities.users.RegisteredUser;
import mjtfinalproject.exceptions.InvalidEntity;
import mjtfinalproject.repositories.userrepository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AddFriendTest {

    @Mock
    private RegisteredUser userMock;
    @Mock
    private UserRepository userRepositoryMock;
    private String[] input;

    private AddFriend addFriend;

    @BeforeEach
    void setUp() {
        input = new String[] {"username"};
        addFriend = new AddFriend(userMock, userRepositoryMock, input);
    }

    @Test
    void testBothValidUsers() {
        RegisteredUser validFriendMock = mock(RegisteredUser.class);
        Optional<RegisteredUser> optionalFriend = Optional.of(validFriendMock);
        when(userRepositoryMock.getUser(addFriend.getFriendToAdd())).thenReturn(optionalFriend);

        assertTrue(addFriend.execute().contains("\"status\":\"OK\""),
            "When successfully adding a friend, a positive message should be returned");

        verify(userMock, times(1)).addFriend(validFriendMock);
        verify(validFriendMock, times(1)).addFriend(userMock);
    }

    @Test
    void testNonexistentFriend() {
        when(userRepositoryMock.getUser(addFriend.getFriendToAdd())).thenReturn(Optional.empty());

        assertTrue(addFriend.execute().contains("\"status\":\"ERROR\""),
            "When user to be added as a friend is not registered, a negative message should be returned.");

        verifyNoInteractions(userMock);
    }

    @Test
    void testAddYourselfAsFriend() {
        Optional<RegisteredUser> optionalFriend = Optional.of(userMock);
        when(userRepositoryMock.getUser(addFriend.getFriendToAdd())).thenReturn(optionalFriend);
        doThrow(InvalidEntity.class).when(userMock).addFriend(userMock);

        assertTrue(addFriend.execute().contains("\"status\":\"ERROR\""),
            "When a user tries to add themselves as a friend, a negative message should be returned.");
    }

}
