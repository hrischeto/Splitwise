package mjt.project.entities.group;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

public interface Group extends Serializable {
    String getName();

    Set<String> getMembers();

    UUID id();
}
