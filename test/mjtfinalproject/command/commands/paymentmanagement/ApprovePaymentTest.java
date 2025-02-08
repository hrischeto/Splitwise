package mjtfinalproject.command.commands.paymentmanagement;

import mjtfinalproject.command.CommandMessages;
import mjtfinalproject.entities.users.RegisteredUser;
import mjtfinalproject.repositories.userrepository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ApprovePaymentTest {

    @Mock
    private RegisteredUser userMock;
    @Mock
    private UserRepository userRepositoryMock;

    private ApprovePayment approvePayment;

    @BeforeEach
    void setup() {
        String[] input = new String[] {"1.0", "paid"};
        approvePayment = new ApprovePayment(userMock, userRepositoryMock, input);
    }

    @Test
    void testWrongNumberArgumentsInInput() {
        String[] wrongLengthInput = new String[] {"one"};
        ApprovePayment wrongInputPayment = new ApprovePayment(userMock, userRepositoryMock, wrongLengthInput);

        assertTrue(wrongInputPayment.execute().contains(CommandMessages.ERROR_MESSAGE.toString()),
            "When user input has wrong number of arguments, a negative message is returned.");
    }

    @Test
    void testIncorrectMoneyFormat() {
        String[] wrongPriceInput = new String[] {"notdouble", "friend"};
        ApprovePayment invalidPricePayment = new ApprovePayment(userMock, userRepositoryMock, wrongPriceInput);

        assertTrue(invalidPricePayment.execute().contains(CommandMessages.ERROR_MESSAGE.toString()),
            "When money to be approved are not in correct format, a negative message should be returned");
    }

    @Test
    void testNegativeAmountToSplit() {
        String[] negativePriceInput = new String[] {"-1", "friend", "reason"};
        ApprovePayment negativePricePayment = new ApprovePayment(userMock, userRepositoryMock, negativePriceInput);

        assertTrue(negativePricePayment.execute().contains(CommandMessages.ERROR_MESSAGE.toString()),
            "When money to be approved are negative, a negative message should be returned");
    }

    @Test
    void testUserNotRegistered() {
        when(userRepositoryMock.getUser("paid")).thenReturn(Optional.empty());

        assertTrue(approvePayment.execute().contains(CommandMessages.ERROR_MESSAGE.toString()),
            "When username is invalid, a negative message is returned.");
    }

    @Test
    void testNoSuchObligation() {
        RegisteredUser paid = mock(RegisteredUser.class);
        when(userRepositoryMock.getUser("paid")).thenReturn(Optional.of(paid));
        when(userMock.markAsPayed(paid, 1.0)).thenReturn(false);

        assertTrue(approvePayment.execute().contains(CommandMessages.ERROR_MESSAGE.toString()),
            "When said user does not have any obligations to the approving user, a negative message is returned.");

        verify(paid, times(0)).removeObligation(userMock, 1.0);
    }

    @Test
    void testCorrectArguments() {
        RegisteredUser paid = mock(RegisteredUser.class);
        when(userRepositoryMock.getUser("paid")).thenReturn(Optional.of(paid));
        when(userMock.markAsPayed(paid, 1.0)).thenReturn(true);

        assertTrue(approvePayment.execute().contains(CommandMessages.OK_MESSAGE.toString()),
            "When said obligation is successfully approved, a positive message is returned.");

        verify(paid, times(1)).removeObligation(userMock, 1.0);
        verify(userMock, times(1)).markAsPayed(paid, 1.0);
    }

}
