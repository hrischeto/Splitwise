package mjtfinalproject.repositories.grouprepository;

import mjtfinalproject.entities.group.Group;
import mjtfinalproject.exceptions.InvalidEntity;
import mjtfinalproject.exceptions.InvalidGroupId;

import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryGroupRepository implements GroupRepository {

    private static final Path GROUP_DATABASE = Path.of("groups.txt");

    private final Map<UUID, Group> groups = new ConcurrentHashMap<>();

    @Override
    public void addGroup(Group group) {
        if (Objects.isNull(group)) {
            throw new InvalidEntity("Null group to add.");
        }

        groups.put(group.id(), group);
    }

    @Override
    public Group getGroup(UUID groupId) {
        if (Objects.isNull(groupId)) {
            throw new InvalidGroupId("Null group id.");
        }

        return groups.getOrDefault(groupId, null);
    }

    @Override
    public void safeToDatabase() {

    }
}
