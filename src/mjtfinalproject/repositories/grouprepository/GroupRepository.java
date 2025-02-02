package mjtfinalproject.repositories.grouprepository;

import mjtfinalproject.entities.group.Group;
import mjtfinalproject.repositories.Repository;

import java.util.UUID;

public interface GroupRepository extends Repository {

    void addGroup(Group group);

    Group getGroup(UUID groupId);

}
