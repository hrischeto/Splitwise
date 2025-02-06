package mjtfinalproject.command.commands.relations;

import mjtfinalproject.command.Command;
import mjtfinalproject.entities.group.Group;
import mjtfinalproject.entities.users.RegisteredUser;
import mjtfinalproject.exceptions.FailedCommandCreationException;
import mjtfinalproject.repositories.grouprepository.GroupRepository;
import mjtfinalproject.repositories.userrepository.UserRepository;

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
        if (Objects.isNull(userRepository) || Objects.isNull(groupRepository) || Objects.isNull(input)) {
            return "\"status\":\"ERROR\", \"message\":\"Invalid input for \"create-group\" command.";
        }

        Set<RegisteredUser> members = new HashSet<>();
        for (int i = 1; i < input.length; i++) {
            Optional<RegisteredUser> user = userRepository.getUser(input[i]);

            if (user.isEmpty()) {
                return "\"status\":\"ERROR\", \"message\":\"User " + input[i] + " does not exist.\"";
            }
            members.add(user.get());
        }

        members.add(creatingUser);
        groupRepository.addGroup(new Group(input[GROUP_NAME_INDEX], members));
        return "\"status\":\"OK\", \"message\":\"Created group \"" + input[GROUP_NAME_INDEX] + "\"!\"";
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
            throw new FailedCommandCreationException("User creating group is null.");
        }
    }
    
}
