package mjtfinalproject.repositories.grouprepository;

import mjtfinalproject.entities.group.Group;
import mjtfinalproject.exceptions.InvalidEntity;
import mjtfinalproject.exceptions.InvalidGroupId;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryGroupRepository implements GroupRepository {

    private static final String GROUP_DATABASE = "groups.txt";

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
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(GROUP_DATABASE))) {
            out.writeObject(this);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to safe group repository.", e);
        }
    }
}
