package mjtfinalproject.repositories.userrepository;

import mjtfinalproject.entities.users.RegisteredUser;
import mjtfinalproject.exceptions.InvalidEntity;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository implements UserRepository {

    private final Map<String, RegisteredUser> registeredUsers = new ConcurrentHashMap<>();

    @Override
    public void addUser(RegisteredUser user) {
        if (Objects.isNull(user)) {
            throw new InvalidEntity("Null user");
        }

        RegisteredUser existing = registeredUsers.putIfAbsent(user.getUsername(), user);

        if (Objects.nonNull(existing)) {
            throw new InvalidEntity("User with that name already exists");
        }
    }

    @Override
    public Optional<RegisteredUser> getUser(String name) {
        if (Objects.isNull(name)) {
            throw new InvalidEntity("Null username");
        }

        if (registeredUsers.containsKey(name)) {
            return Optional.of(registeredUsers.get(name));
        }

        return Optional.empty();
    }

    @Override
    public void safeToDatabase(String database) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(database))) {
            out.writeObject(this);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to safe user repository.", e);
        }
    }

}
