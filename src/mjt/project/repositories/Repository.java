package mjt.project.repositories;

import java.io.Serializable;

public interface Repository extends Serializable {

    void safeToDatabase(String database);

}
