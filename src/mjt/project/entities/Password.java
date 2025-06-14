package mjt.project.entities;

import java.io.Serializable;

public record Password(String password, byte[] salt) implements Serializable {
}
