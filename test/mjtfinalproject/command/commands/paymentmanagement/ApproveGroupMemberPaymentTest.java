package mjtfinalproject.command.commands.paymentmanagement;

import mjtfinalproject.command.CommandMessages;
import mjtfinalproject.entities.group.Group;
import mjtfinalproject.entities.users.RegisteredUser;
import mjtfinalproject.exceptions.InvalidGroupId;
import mjtfinalproject.repositories.grouprepository.GroupRepository;
import mjtfinalproject.repositories.userrepository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ApproveGroupMemberPaymentTest {

    @Mock
    private RegisteredUser userMock;
    @Mock
    private UserRepository userRepositoryMock;
    @Mock
    private GroupRepository groupRepositoryMock;

    private ApproveGroupMemberPayment approveGroupMemberPayment;

    @BeforeEach
    void setUp() {
        String[] input = new String[] {"1.0", "user", "group"};
        approveGroupMemberPayment =
            new ApproveGroupMemberPayment(userMock, groupRepositoryMock, userRepositoryMock, input);
    }

    @Test
    void testWrongNumberArgumentsInInput() {
        String[] wrongLengthInput = new String[] {"one"};
        ApproveGroupMemberPayment wrongInputPayment =
            new ApproveGroupMemberPayment(userMock, groupRepositoryMock, userRepositoryMock, wrongLengthInput);

        assertTrue(wrongInputPayment.execute().contains(CommandMessages.ERROR_MESSAGE.toString()),
            "When user input has wrong number of arguments, a negative message is returned.");
    }

    @Test
    void testIncorrectMoneyFormat() {
        String[] wrongPriceInput = new String[] {"notdouble", "user", "group"};
        ApproveGroupMemberPayment invalidPricePayment =
            new ApproveGroupMemberPayment(userMock, groupRepositoryMock, userRepositoryMock, wrongPriceInput);

        assertTrue(invalidPricePayment.execute().contains(CommandMessages.ERROR_MESSAGE.toString()),
            "When money to be approved are not in correct format, a negative message should be returned");
    }

    @Test
    void testNegativeAmountToSplit() {
        String[] negativePriceInput = new String[] {"-1", "user", "group"};
        ApproveGroupMemberPayment negativePricePayment =
            new ApproveGroupMemberPayment(userMock, groupRepositoryMock, userRepositoryMock, negativePriceInput);

        assertTrue(negativePricePayment.execute().contains(CommandMessages.ERROR_MESSAGE.toString()),
            "When money to be approved are negative, a negative message should be returned");
    }

    @Test
    void testUserNotRegistered() {
        when(userRepositoryMock.getUser("user")).thenReturn(Optional.empty());

        assertTrue(approveGroupMemberPayment.execute().contains(CommandMessages.ERROR_MESSAGE.toString()),
            "When username is invalid, a negative message is returned.");
    }

    @Test
    void testUserHasNoSuchGroup() {
        RegisteredUser paid = mock(RegisteredUser.class);
        when(userRepositoryMock.getUser("user")).thenReturn(Optional.of(paid));
        when(userMock.getGroup("group")).thenReturn(null);

        assertTrue(approveGroupMemberPayment.execute().contains(CommandMessages.ERROR_MESSAGE.toString()),
            "When user does not participate in said group, a negative message is returned.");

        verify(paid, times(0)).removeObligationInGroup(any(), any(), anyDouble());
        verify(userMock, times(0)).markAsPayedFromGroupMember(any(), any(), anyDouble());
    }

    @Test
    void testInvalidGroup() {
        RegisteredUser paid = mock(RegisteredUser.class);
        when(userRepositoryMock.getUser("user")).thenReturn(Optional.of(paid));
        UUID id = mock(UUID.class);
        when(userMock.getGroup("group")).thenReturn(id);
        when(groupRepositoryMock.getGroup(id)).thenReturn(null);

        assertThrows(InvalidGroupId.class,
            () -> approveGroupMemberPayment.execute().contains(CommandMessages.ERROR_MESSAGE.toString()),
            "If user has a group but it is not in the repository, an exception is thrown.");

        verify(paid, times(0)).removeObligationInGroup(any(), any(), anyDouble());
        verify(userMock, times(0)).markAsPayedFromGroupMember(any(), any(), anyDouble());
    }

    @Test
    void testNoSuchObligation() {
        RegisteredUser paid = mock(RegisteredUser.class);
        when(userRepositoryMock.getUser("user")).thenReturn(Optional.of(paid));
        UUID id = mock(UUID.class);
        when(userMock.getGroup("group")).thenReturn(id);
        Group group = mock(Group.class);
        when(groupRepositoryMock.getGroup(id)).thenReturn(group);
        when(userMock.markAsPayedFromGroupMember(group, paid, 1.0)).thenReturn(false);

        assertTrue(approveGroupMemberPayment.execute().contains(CommandMessages.ERROR_MESSAGE.toString()),
            "When said user does not have any obligations to the approving user, a negative message is returned.");

        verify(paid, times(0)).removeObligationInGroup(group, userMock, 1.0);
    }

    @Test
    void testCorrectArguments() {
        RegisteredUser paid = mock(RegisteredUser.class);
        when(userRepositoryMock.getUser("user")).thenReturn(Optional.of(paid));
        UUID id = mock(UUID.class);
        when(userMock.getGroup("group")).thenReturn(id);
        Group group = mock(Group.class);
        when(groupRepositoryMock.getGroup(id)).thenReturn(group);
        when(userMock.markAsPayedFromGroupMember(group, paid, 1.0)).thenReturn(true);

        assertTrue(approveGroupMemberPayment.execute().contains(CommandMessages.OK_MESSAGE.toString()),
            "When said obligation is successfully approved, a positive message is returned.");

        verify(paid, times(1)).removeObligationInGroup(group, userMock, 1.0);
    }

}
