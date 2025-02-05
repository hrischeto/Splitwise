package mjtfinalproject.entities.group;

import mjtfinalproject.entities.users.RegisteredUser;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public record Group(UUID id, String name, Set<RegisteredUser> participants) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return Objects.equals(id, group.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
