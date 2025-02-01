package mjtfinalproject.repositories.userrepository;

import mjtfinalproject.entities.users.RegisteredUser;
import mjtfinalproject.entities.users.User;
import mjtfinalproject.exceptions.InvalidEntity;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository implements UserRepository {

    private static final Path USER_DATABASE = Path.of("users.txt");

    private final Set<RegisteredUser> registeredUsers = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @Override
    public void addUser(RegisteredUser user) {
        if (Objects.isNull(user)) {
            throw new InvalidEntity("Null");
        }
    }

    @Override
    public RegisteredUser getUser(String name) {

    }

    @Override
    public void safeToDatabase() {

    }
}
