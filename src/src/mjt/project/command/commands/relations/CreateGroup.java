package mjt.project.command.commands.relations;

import mjt.project.command.factory.Command;
import mjt.project.command.CommandMessages;
import mjt.project.entities.group.Group;
import mjt.project.entities.group.GroupImpl;
import mjt.project.entities.users.RegisteredUser;
import mjt.project.exceptions.FailedCommandCreationException;
import mjt.project.repositories.grouprepository.GroupRepository;
import mjt.project.repositories.userrepository.UserRepository;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class CreateGroup implements Command {

    private static final int MINIMUM_INPUT_LENGTH = 3;
    private static final int GROUP_NAME_INDEX = 0;

    private final String[] input;
    private final RegisteredUser creatingUser;

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public CreateGroup(UserRepository userRepository, GroupRepository groupRepository, RegisteredUser creatingUser,
                       String... input) {
        validateArguments(userRepository, groupRepository, creatingUser, input);

        if (input.length < MINIMUM_INPUT_LENGTH) {
            this.userRepository = null;
            this.groupRepository = null;
            this.creatingUser = null;
            this.input = null;
        } else {
            this.userRepository = userRepository;
            this.groupRepository = groupRepository;
            this.creatingUser = creatingUser;
            this.input = input;
        }
    }

    @Override
    public String execute() {
        if (Objects.isNull(input)) {
            return CommandMessages.ERROR_MESSAGE + " \"message\":\"Invalid input for \"create-group\" command.";
        }

        Set<RegisteredUser> members = new HashSet<>();
        String membersValidation = getMembers(members);
        if (Objects.nonNull(membersValidation)) {
            return membersValidation;
        }

        Group group = new GroupImpl(input[GROUP_NAME_INDEX], members);
        groupRepository.addGroup(group);

        for (RegisteredUser member : members) {
            member.addGroup(group);
        }
        return CommandMessages.OK_MESSAGE + " \"message\":\"Created group \"" + input[GROUP_NAME_INDEX] + "\"!\"";
    }

    private String getMembers(Set<RegisteredUser> members) {
        if (Objects.isNull(members)) {
            throw new IllegalArgumentException("Null member set");
        }

        for (int i = 1; i < input.length; i++) {
            Optional<RegisteredUser> user = userRepository.getUser(input[i]);

            if (user.isEmpty()) {
                return CommandMessages.ERROR_MESSAGE + " \"message\":\"User " + input[i] + " does not exist.\"";
            }
            if (!user.get().isGroupNameUnique(input[GROUP_NAME_INDEX])) {
                return CommandMessages.ERROR_MESSAGE + " \"message\":\"User " + user.get().getUsername() +
                    " already has a group with that name.\"";
            }
            members.add(user.get());
        }

        if (!creatingUser.isGroupNameUnique(input[GROUP_NAME_INDEX])) {
            return CommandMessages.ERROR_MESSAGE + " \"message\":\"You already have a group with this name\"";
        }
        members.add(creatingUser);

        return null;
    }

    private void validateArguments(UserRepository userRepository, GroupRepository groupRepository,
                                   RegisteredUser creatingUser, String... input) {
        if (Objects.isNull(userRepository)) {
            throw new FailedCommandCreationException("User repository was null.");
        }
        if (Objects.isNull(groupRepository)) {
            throw new FailedCommandCreationException("Group repository was null.");
        }
        if (Objects.isNull(input)) {
            throw new FailedCommandCreationException("User input was null.");
        }
        if (Objects.isNull(creatingUser)) {
            throw new FailedCommandCreationException("User was null");
        }
    }

}
