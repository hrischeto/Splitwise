package mjtfinalproject.command.commands.paymentmanagement;

import mjtfinalproject.command.factory.Command;
import mjtfinalproject.command.CommandMessages;
import mjtfinalproject.entities.users.RegisteredUser;
import mjtfinalproject.exceptions.FailedCommandCreationException;
import mjtfinalproject.repositories.userrepository.UserRepository;

import java.util.Objects;
import java.util.Optional;

public class ApprovePayment implements Command {

    private static final int INPUT_LENGTH = 2;
    private static final int AMOUNT_INDEX = 0;
    private static final int USER_INDEX = 1;

    private final String[] input;
    private final RegisteredUser user;
    private final UserRepository userRepository;

    public ApprovePayment(RegisteredUser user, UserRepository userRepository, String... input) {
        validateArguments(user, userRepository, input);

        if (input.length != INPUT_LENGTH) {
            this.input = null;
            this.user = null;
            this.userRepository = null;
        } else {
            this.input = input;
            this.user = user;
            this.userRepository = userRepository;
        }
    }

    @Override
    public String execute() {
        if (Objects.isNull(user) || Objects.isNull(input)) {
            return CommandMessages.ERROR_MESSAGE + " \"message\":\"Invalid input for \"approve-payment\" command.";
        }

        double amount = getAmount();
        if (amount < 0.0) {
            return CommandMessages.ERROR_MESSAGE + "\"message\": \"Invalid amount.\"";
        }

        Optional<RegisteredUser> paid = userRepository.getUser(input[USER_INDEX]);
        if (paid.isEmpty()) {
            return CommandMessages.ERROR_MESSAGE + "\"message\": \"No such user exists.\"";
        }
        if (!user.isFriend(paid.get())) {
            return CommandMessages.ERROR_MESSAGE + "\"message\": You are not friends with \"" +
                paid.get().getUsername() + ".\"";
        }

        boolean paymentApproved = user.markAsPayedFromFriend(paid.get(), amount);
        if (!paymentApproved) {
            return CommandMessages.ERROR_MESSAGE + "\"message\": \"" + paid.get().getUsername() +
                " did not have any obligations to you.\"";
        }

        paid.get().removeObligationToFriend(user, amount);

        return CommandMessages.OK_MESSAGE + "\"message\":\"Payment approved.\"";
    }

    private double getAmount() {
        double result;
        try {
            result = Double.parseDouble(input[AMOUNT_INDEX]);
        } catch (NumberFormatException e) {
            return -1.0;
        }
        if (result < 0.0) {
            return -1.0;
        }

        return result;
    }

    private void validateArguments(RegisteredUser user, UserRepository userRepository, String[] input) {
        if (Objects.isNull(user)) {
            throw new FailedCommandCreationException("Null user");
        }
        if (Objects.isNull(input)) {
            throw new FailedCommandCreationException("Null input.");
        }
        if (Objects.isNull(userRepository)) {
            throw new FailedCommandCreationException("Null user repository.");
        }
    }
}
