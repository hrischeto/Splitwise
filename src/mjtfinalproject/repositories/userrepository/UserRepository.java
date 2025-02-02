package mjtfinalproject.repositories.userrepository;

import mjtfinalproject.entities.users.RegisteredUser;
import mjtfinalproject.repositories.Repository;

import java.util.Optional;
import java.util.Set;

public interface UserRepository extends Repository {

    void addUser(RegisteredUser user);

    Optional<RegisteredUser> getUser(String username);

    Set<RegisteredUser> getAllUsers();
}
