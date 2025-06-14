package mjt.project.command.commands.split;

import mjt.project.command.factory.Command;
import mjt.project.command.CommandMessages;
import mjt.project.entities.group.Group;
import mjt.project.entities.users.RegisteredUser;
import mjt.project.exceptions.FailedCommandCreationException;
import mjt.project.obligation.Obligation;
import mjt.project.repositories.grouprepository.GroupRepository;
import mjt.project.repositories.userrepository.UserRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.UUID;

public class SplitGroup implements Command {

    private static final int INPUT_LENGTH = 3;
    private static final int AMOUNT_INDEX = 0;
    private static final int GROUPNAME_INDEX = 1;
    private static final int REASON_INDEX = 2;

    private final String[] input;
    private final GroupRepository groupRepository;
    private final RegisteredUser payingUser;
    private final UserRepository userRepository;

    public SplitGroup(GroupRepository groupRepository, UserRepository userRepository, RegisteredUser payingUser,
                      String... input) {
        validateArguments(groupRepository, payingUser, input);

        if (input.length != INPUT_LENGTH) {
            this.input = null;
            this.groupRepository = null;
            this.userRepository = null;
            this.payingUser = null;
        } else {
            this.input = input;
            this.groupRepository = groupRepository;
            this.userRepository = userRepository;
            this.payingUser = payingUser;
        }
    }

    @Override
    public String execute() {
        if (input == null || payingUser == null || groupRepository == null) {
            return CommandMessages.ERROR_MESSAGE + "\"message\": Invalid input for \"split-group\" command.";
        }

        double amount = getAmount();
        if (amount < 0.0) {
            return CommandMessages.ERROR_MESSAGE + "\"message\": Invalid amount.";
        }

        UUID groupId = payingUser.getGroup(input[GROUPNAME_INDEX]);
        if (groupId == null) {
            return CommandMessages.ERROR_MESSAGE + " \"message\" : \"You do not participate in such group.\"";
        }

        Group group = groupRepository.getGroup(groupId);
        splitAmount(amount, group, input[REASON_INDEX]);

        return CommandMessages.OK_MESSAGE + " \"message\" : \"" + amount + "LV split between you and " +
            group.getName() + " for " + input[REASON_INDEX] + ".\"";
    }

    private void splitAmount(double amount, Group group, String reason) {
        BigDecimal amountToPay = new BigDecimal(amount / group.getMembers().size()).setScale(2, RoundingMode.CEILING);

        for (String member : group.getMembers()) {
            if (member.equals(payingUser.getUsername())) {
                continue;
            }

            RegisteredUser user = userRepository.getUser(member).get();
            user.addNewObligationInGroup(group,
                new Obligation(payingUser.getUsername(), amountToPay.doubleValue(), reason));
            payingUser.addNewWaitingPaymentFromGroupMember(group, user, amountToPay.doubleValue());
        }
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

    private void validateArguments(GroupRepository groupRepository,
                                   RegisteredUser payingUser,
                                   String... input) {
        if (Objects.isNull(groupRepository)) {
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
