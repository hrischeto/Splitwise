package mjtfinalproject.entities.group;

import mjtfinalproject.entities.users.RegisteredUser;

import java.util.Set;
import java.util.UUID;

public interface Group {
    String getName();

    Set<RegisteredUser> getMembers();

    UUID id();

    void splitAmount(double amount, RegisteredUser payingUser, String reason);
}
