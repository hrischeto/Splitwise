package mjt.project.command.commands.split;

import mjt.project.command.CommandMessages;
import mjt.project.entities.group.Group;
import mjt.project.entities.users.RegisteredUser;
import mjt.project.repositories.grouprepository.GroupRepository;
import mjt.project.repositories.userrepository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SplitGroupTest {

    @Mock
    private GroupRepository groupRepositoryMock;
    @Mock
    private RegisteredUser payingUserMock;
    @Mock
    UserRepository userRepositoryMock;

    private SplitGroup splitGroup;

    @BeforeEach
    void setUp() {
        String[] input = new String[] {"40", "family", "reason"};
        splitGroup = new SplitGroup(groupRepositoryMock, userRepositoryMock, payingUserMock, input);
    }

    @Test
    void testNotEnoughArgumentsInInput() {
        String[] wrongLengthInput = new String[] {"reason"};
        SplitGroup wrongInputSplitGroup =
            new SplitGroup(groupRepositoryMock, userRepositoryMock, payingUserMock, wrongLengthInput);

        assertTrue(wrongInputSplitGroup.execute().contains(CommandMessages.ERROR_MESSAGE.toString()),
            "When user input has wrong number of arguments, a negative message is returned.");
    }

    @Test
    void testIncorrectMoneyFormat() {
        String[] wrongPriceInput = new String[] {"notdouble", "group", "reason"};
        SplitGroup invalidPriceSplitGroup =
            new SplitGroup(groupRepositoryMock, userRepositoryMock, payingUserMock, wrongPriceInput);

        assertTrue(invalidPriceSplitGroup.execute().contains(CommandMessages.ERROR_MESSAGE.toString()),
            "When money to be split are not in correct format, a negative message should be returned");
    }

    @Test
    void testNegativeAmountToSplit() {
        String[] negativePriceInput = new String[] {"-1.0", "friend", "reason"};
        SplitGroup negativePriceSplitGroup =
            new SplitGroup(groupRepositoryMock, userRepositoryMock, payingUserMock, negativePriceInput);

        assertTrue(negativePriceSplitGroup.execute().contains(CommandMessages.ERROR_MESSAGE.toString()),
            "When money to be split are negative, a negative message should be returned");
    }

    @Test
    void testNoSuchGroupForUser() {
        when(payingUserMock.getGroup("family")).thenReturn(null);
        assertTrue(splitGroup.execute().contains(CommandMessages.ERROR_MESSAGE.toString()),
            "When user does not participate in said group, a negative message is returned.");
    }

    @Test
    void testCorrectArguments() {
        Group group = mock(Group.class);
        UUID id = mock(UUID.class);
        Set<String> members = Set.of("User1", "User2");
        RegisteredUser user1 = mock(RegisteredUser.class);
        RegisteredUser user2 = mock(RegisteredUser.class);

        when(payingUserMock.getGroup("family")).thenReturn(id);
        when(groupRepositoryMock.getGroup(id)).thenReturn(group);
        when(userRepositoryMock.getUser("User1")).thenReturn(Optional.of(user1));
        when(userRepositoryMock.getUser("User2")).thenReturn(Optional.of(user2));
        when(group.getMembers()).thenReturn(members);

        assertTrue(splitGroup.execute().contains(CommandMessages.OK_MESSAGE.toString()),
            "When passed valid arguments, a positive message is returned.");
    }
}
