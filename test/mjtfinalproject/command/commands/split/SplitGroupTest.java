package mjtfinalproject.command.commands.split;

import mjtfinalproject.command.CommandMessages;
import mjtfinalproject.entities.group.Group;
import mjtfinalproject.entities.users.RegisteredUser;
import mjtfinalproject.repositories.grouprepository.GroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SplitGroupTest {

    @Mock
    private GroupRepository groupRepositoryMock;
    @Mock
    private RegisteredUser payingUserMock;

    private SplitGroup splitGroup;

    @BeforeEach
    void setUp() {
        String[] input = new String[] {"1", "family", "reason"};
        splitGroup = new SplitGroup(groupRepositoryMock, payingUserMock, input);
    }

    @Test
    void testNotEnoughArgumentsInInput() {
        String[] wrongLengthInput = new String[] {"reason"};
        SplitGroup wrongInputSplitGroup = new SplitGroup(groupRepositoryMock, payingUserMock, wrongLengthInput);

        assertTrue(wrongInputSplitGroup.execute().contains(CommandMessages.ERROR_MESSAGE.toString()),
            "When user input has wrong number of arguments, a negative message is returned.");
    }

    @Test
    void testIncorrectMoneyFormat() {
        String[] wrongPriceInput = new String[] {"notdouble", "group", "reason"};
        SplitGroup invalidPriceSplitGroup = new SplitGroup(groupRepositoryMock, payingUserMock, wrongPriceInput);

        assertTrue(invalidPriceSplitGroup.execute().contains(CommandMessages.ERROR_MESSAGE.toString()),
            "When money to be split are not in correct format, a negative message should be returned");
    }

    @Test
    void testNegativeAmountToSplit() {
        String[] negativePriceInput = new String[] {"-1.0", "friend", "reason"};
        SplitGroup negativePriceSplitGroup = new SplitGroup(groupRepositoryMock, payingUserMock, negativePriceInput);

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

        when(payingUserMock.getGroup("family")).thenReturn(id);
        when(groupRepositoryMock.getGroup(id)).thenReturn(group);

        assertTrue(splitGroup.execute().contains(CommandMessages.OK_MESSAGE.toString()), "When passed valid arguments, a positive message is returned.");

        verify(group, times(1)).splitAmount(1.0, payingUserMock, "reason");
    }
}
