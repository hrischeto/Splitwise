package mjt.project.repositories.grouprepository;

import mjt.project.entities.group.Group;
import mjt.project.exceptions.InvalidEntity;
import mjt.project.exceptions.InvalidGroupId;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryGroupRepository implements GroupRepository {

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
    public void safeToDatabase(String database) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(database))) {
            out.writeObject(this);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to safe group repository.", e);
        }
    }

}
