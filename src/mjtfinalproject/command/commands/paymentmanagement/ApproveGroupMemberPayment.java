package mjtfinalproject.command.commands.paymentmanagement;

import mjtfinalproject.command.Command;
import mjtfinalproject.command.CommandMessages;
import mjtfinalproject.entities.group.Group;
import mjtfinalproject.entities.users.RegisteredUser;
import mjtfinalproject.exceptions.FailedCommandCreationException;
import mjtfinalproject.exceptions.InvalidGroupId;
import mjtfinalproject.repositories.grouprepository.GroupRepository;
import mjtfinalproject.repositories.userrepository.UserRepository;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class ApproveGroupMemberPayment implements Command {

    private static final int INPUT_LENGTH = 3;
    private static final int AMOUNT_INDEX = 0;
    private static final int USER_INDEX = 1;
    private static final int GROUP_INDEX = 2;

    private final String[] input;
    private final RegisteredUser user;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    public ApproveGroupMemberPayment(RegisteredUser user, GroupRepository groupRepository,
                                     UserRepository userRepository, String... input) {
        validateArguments(user, groupRepository, userRepository, input);

        if (input.length != INPUT_LENGTH) {
            this.input = null;
            this.user = null;
            this.userRepository = null;
            this.groupRepository = null;
        } else {
            this.input = input;
            this.user = user;
            this.userRepository = userRepository;
            this.groupRepository = groupRepository;
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
        Group group = getGroup();
        if (Objects.isNull(group)) {
            return CommandMessages.ERROR_MESSAGE + "\"message\": You do not participate in such group.\"";
        }

        boolean paymentApproved = user.markAsPayedFromGroupMember(group, paid.get(), amount);
        if (!paymentApproved) {
            return CommandMessages.ERROR_MESSAGE + "\"message\": \"" + paid.get().getUsername() +
                " did not have any obligations to you in this group.\"";
        }

        paid.get().removeObligationInGroup(group, user, amount);

        return CommandMessages.OK_MESSAGE + "\"message\":\"Payment approved.\"";
    }

    private Group getGroup() {

        UUID groupId = user.getGroup(input[GROUP_INDEX]);
        if (Objects.isNull(groupId)) {
            return null;
        }

        Group group = groupRepository.getGroup(groupId);
        if (Objects.isNull(group)) {
            throw new InvalidGroupId("User's group was not found in the group repository.");
        }

        return group;
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

    private void validateArguments(RegisteredUser user, GroupRepository groupRepository, UserRepository userRepository,
                                   String[] input) {
        if (Objects.isNull(user)) {
            throw new FailedCommandCreationException("Null user");
        }
        if (Objects.isNull(input)) {
            throw new FailedCommandCreationException("Null input.");
        }
        if (Objects.isNull(userRepository)) {
            throw new FailedCommandCreationException("Null user repository.");
        }
        if (Objects.isNull(groupRepository)) {
            throw new FailedCommandCreationException("Null group repository.");
        }
    }
}
