package mjtfinalproject.repository;

import mjtfinalproject.entities.Group;
import mjtfinalproject.exceptions.InvalidEntity;
import mjtfinalproject.exceptions.InvalidGroupId;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryGroupRepository {

    private final Map<UUID, Group> groups = new ConcurrentHashMap<>();

    public void addGroup(Group group) {
        if (Objects.isNull(group)) {
            throw new InvalidEntity("Null group to add.");
        }

        groups.put(group.id(), group);
    }

    public Group getGroup(UUID groupId) {
        if (Objects.isNull(groupId)) {
            throw new InvalidGroupId("Null group id.");
        }

        return groups.getOrDefault(groupId, null);
    }
}
