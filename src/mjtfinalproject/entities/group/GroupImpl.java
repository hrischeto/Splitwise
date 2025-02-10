package mjtfinalproject.entities.group;

import mjtfinalproject.entities.users.RegisteredUser;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class GroupImpl implements Group {

    private final UUID id;
    private final String name;
    private final Set<String> members;

    public GroupImpl(String name, Set<RegisteredUser> members) {
        validateArguments(name, members);

        this.id = UUID.randomUUID();
        this.name = name;

        this.members = Collections.newSetFromMap(new ConcurrentHashMap<>());
        this.members.addAll(members.stream()
            .map(RegisteredUser::getUsername)
            .collect(Collectors.toSet()));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Set<String> getMembers() {
        return Collections.unmodifiableSet(members);
    }

    @Override
    public UUID id() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupImpl group = (GroupImpl) o;
        return Objects.equals(id, group.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    private void validateArguments(String name, Set<RegisteredUser> members) {
        if (Objects.isNull(name)) {
            throw new IllegalArgumentException("Group name was null.");
        }

        if (Objects.isNull(members)) {
            throw new IllegalArgumentException("Set of members was null.");
        }
    }
}