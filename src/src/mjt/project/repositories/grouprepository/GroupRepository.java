package mjt.project.repositories.grouprepository;

import mjt.project.entities.group.Group;
import mjt.project.repositories.Repository;

import java.util.UUID;

public interface GroupRepository extends Repository {

    void addGroup(Group group);

    Group getGroup(UUID groupId);

}
