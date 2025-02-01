package mjtfinalproject.repository;

import mjtfinalproject.entities.users.User;
import mjtfinalproject.exceptions.InvalidEntity;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository {

    private final Set<User> registeredUsers = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public void addUser(User user) {
if(Objects.isNull(user)){
    throw new InvalidEntity("Null")
}
    }

    public User getUser(String name) {

    }
}
