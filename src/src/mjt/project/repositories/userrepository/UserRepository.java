package mjt.project.repositories.userrepository;

import mjt.project.entities.users.RegisteredUser;
import mjt.project.repositories.Repository;

import java.util.Optional;

public interface UserRepository extends Repository {

    void addUser(RegisteredUser user);

    Optional<RegisteredUser> getUser(String username);

}
