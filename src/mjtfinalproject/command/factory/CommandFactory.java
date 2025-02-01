package mjtfinalproject.command.factory;

import mjtfinalproject.command.Command;
import mjtfinalproject.repositories.Repository;
import mjtfinalproject.repositories.grouprepository.GroupRepository;
import mjtfinalproject.repositories.userrepository.UserRepository;

import java.util.Objects;

public class CommandFactory {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public CommandFactory(GroupRepository groupRepository, UserRepository userRepository) {
        validateRepository(groupRepository);
        validateRepository(userRepository);

        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }

    public Command newCommand(String input) {

    }

    void validateRepository(Repository repo) {
        if (Objects.isNull(repo)) {
            throw new IllegalArgumentException("Null repository.");
        }
    }
}
