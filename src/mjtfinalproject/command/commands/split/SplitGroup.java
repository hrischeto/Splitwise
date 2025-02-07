package mjtfinalproject.command.commands.split;

import mjtfinalproject.command.Command;
import mjtfinalproject.command.CommandMessages;
import mjtfinalproject.entities.group.Group;
import mjtfinalproject.entities.users.RegisteredUser;
import mjtfinalproject.exceptions.FailedCommandCreationException;
import mjtfinalproject.repositories.grouprepository.GroupRepository;

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

    public SplitGroup(GroupRepository groupRepository, RegisteredUser payingUser,
                      String... input) {
        validateArguments(groupRepository, payingUser, input);

        if (input.length != INPUT_LENGTH) {
            this.input = null;
            this.groupRepository = null;
            this.payingUser = null;
        } else {
            this.input = input;
            this.groupRepository = groupRepository;
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
        group.splitAmount(amount, payingUser, input[REASON_INDEX]);

        return CommandMessages.OK_MESSAGE + " \"message\" : " + amount + "LV split between you and " +
            group.getName();
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
