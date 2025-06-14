package mjt.project.command.commands.split;

import mjt.project.command.factory.Command;
import mjt.project.command.CommandMessages;
import mjt.project.entities.users.RegisteredUser;
import mjt.project.exceptions.FailedCommandCreationException;
import mjt.project.obligation.Obligation;
import mjt.project.repositories.userrepository.UserRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.Optional;

public class Split implements Command {

    private static final int INPUT_LENGTH = 3;
    private static final int AMOUNT_INDEX = 0;
    private static final int USERNAME_INDEX = 1;
    private static final int REASON_INDEX = 2;

    private final String[] input;

    private final UserRepository userRepository;
    private final RegisteredUser payingUser;

    public Split(RegisteredUser payingUser, UserRepository userRepository, String... input) {
        validateArguments(payingUser, userRepository, input);

        if (input.length != INPUT_LENGTH) {
            this.input = null;
            this.userRepository = null;
            this.payingUser = null;
        } else {
            this.input = input;
            this.userRepository = userRepository;
            this.payingUser = payingUser;
        }
    }

    @Override
    public String execute() {
        if (input == null || userRepository == null || payingUser == null) {
            return CommandMessages.ERROR_MESSAGE + "\"message\": \"Invalid input for \"split\" command.\"";
        }

        double amount = getAmount();
        if (amount < 0.0) {
            return CommandMessages.ERROR_MESSAGE + "\"message\": \"Invalid amount.\"";
        }

        Optional<RegisteredUser> friend = userRepository.getUser(input[USERNAME_INDEX]);
        if (friend.isEmpty()) {
            return CommandMessages.ERROR_MESSAGE + " \"message\" : \"No such user.\"";
        }
        if (!payingUser.isFriend(friend.get())) {
            return CommandMessages.ERROR_MESSAGE + " \"message\" : \"You are not friends with" +
                friend.get().getUsername() + "\"";
        }

        BigDecimal amountToPay = new BigDecimal(amount / 2).setScale(2, RoundingMode.CEILING);

        friend.get()
            .addNewObligationToFriend(
                new Obligation(payingUser.getUsername(), amountToPay.doubleValue(), input[REASON_INDEX]));
        payingUser.addNewWaitingPaymentFromFriend(friend.get(),
            amountToPay.doubleValue());

        return CommandMessages.OK_MESSAGE + " \"message\" : \"" + amount + "LV split between you and " +
            friend.get().getUsername() + " for " + input[REASON_INDEX] + " \"";
    }

    private double getAmount() {
        double result;
        try {
            result = Double.parseDouble(input[AMOUNT_INDEX]);
        } catch (NumberFormatException e) {
            return -1;
        }
        if (result < 0.0) {
            return -1;
        }

        return result;
    }

    private void validateArguments(RegisteredUser payingUser, UserRepository userRepository, String... input) {
        if (Objects.isNull(userRepository)) {
            throw new FailedCommandCreationException("User repository was null.");
        }
        if (Objects.isNull(input)) {
            throw new FailedCommandCreationException("User input was null.");
        }
        if (Objects.isNull(payingUser)) {
            throw new FailedCommandCreationException("User was null");
        }
    }
}
