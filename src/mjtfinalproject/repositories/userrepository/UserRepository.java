package mjtfinalproject.repositories.userrepository;

import mjtfinalproject.entities.users.RegisteredUser;
import mjtfinalproject.repositories.Repository;

public interface UserRepository extends Repository {

    void addUser(RegisteredUser user);

    RegisteredUser getUser(String username);

}
