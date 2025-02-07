package mjtfinalproject.command.commands.split;

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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SplitTest {

    private static final int USERNAME_INDEX = 1;

    private String[] input;
    @Mock
    private UserRepository userRepositoryMock;
    @Mock
    private RegisteredUser payingUserMock;

    private Split split;

    @BeforeEach
    void setUp() {
        input = new String[] {"1", "friend", "reason"};
        split = new Split(payingUserMock, userRepositoryMock, input);
    }

    @Test
    void testNotEnoughArgumentsInInput() {
        String[] wrongLengthInput = new String[] {"friend"};
        Split wrongInputSplit = new Split(payingUserMock, userRepositoryMock, wrongLengthInput);

        assertTrue(wrongInputSplit.execute().contains(CommandMessages.ERROR_MESSAGE.toString()),
            "When user input has wrong number of arguments, a negative message is returned.");
    }

    @Test
    void testIncorrectMoneyFormat() {
        String[] wrongPriceInput = new String[] {"notdouble", "friend", "reason"};
        Split invalidPriceSplit = new Split(payingUserMock, userRepositoryMock, wrongPriceInput);

        assertTrue(invalidPriceSplit.execute().contains(CommandMessages.ERROR_MESSAGE.toString()),
            "When money to be split are not in correct format, a negative message should be returned");
    }

    @Test
    void testNegativeAmountToSplit() {
        String[] negativePriceInput = new String[] {"-1.0", "friend", "reason"};
        Split negativePriceSplit = new Split(payingUserMock, userRepositoryMock, negativePriceInput);

        assertTrue(negativePriceSplit.execute().contains(CommandMessages.ERROR_MESSAGE.toString()),
            "When money to be split are negative, a negative message should be returned");
    }

    @Test
    void testNotRegisteredFriend() {
        when(userRepositoryMock.getUser(input[USERNAME_INDEX])).thenReturn(Optional.empty());

        assertTrue(split.execute().contains(CommandMessages.ERROR_MESSAGE.toString()),
            "When splitting with a non registered user, a negative message should be returned");

    }

    @Test
    void testNotFriend() {
        RegisteredUser nonFriend = mock(RegisteredUser.class);
        when(userRepositoryMock.getUser(input[USERNAME_INDEX])).thenReturn(Optional.of(nonFriend));
        when(payingUserMock.isFriend(nonFriend)).thenReturn(false);

        assertTrue(split.execute().contains(CommandMessages.ERROR_MESSAGE.toString()),
            "When splitting with a user which is not a friend, a negative message should be returned");

        verify(payingUserMock, times(0)).addNewWaitingPayment(any(), anyDouble());
        verify(nonFriend, times(0)).addNewObligation(any());
    }

    @Test
    void testCorrectArguments() {
        RegisteredUser friend = mock(RegisteredUser.class);
        when(userRepositoryMock.getUser(input[USERNAME_INDEX])).thenReturn(Optional.of(friend));
        when(payingUserMock.isFriend(friend)).thenReturn(true);

        assertTrue(split.execute().contains(CommandMessages.OK_MESSAGE.toString()),
            "When all arguments are correct, a positive message should be returned");

        verify(payingUserMock, times(1)).addNewWaitingPayment(any(), anyDouble());
        verify(friend, times(1)).addNewObligation(any());
    }
}
