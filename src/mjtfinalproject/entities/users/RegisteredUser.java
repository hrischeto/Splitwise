package mjtfinalproject.entities.users;

import mjtfinalproject.entities.Group;
import mjtfinalproject.obligation.Obligation;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class RegisteredUser {

    private final String name;
    private final int password;
    private final Set<Obligation> obligationsToPay;
    private final Set<Obligation> paymentsToReceive;

    private final Set<RegisteredUser> friends;
    private final Set<Group> groups;

    public RegisteredUser(String name, String password) {
        validateArguments(name, password);

        this.name = name;
        this.password = password.hashCode();

        obligationsToPay = new HashSet<>();
        paymentsToReceive = new HashSet<>();

        friends = new HashSet<>();
        groups = new HashSet<>();
    }

    public void addFriend() {

    }

    private void validateArguments(String name, String password) {
        if (Objects.isNull(name)) {
            throw new IllegalArgumentException("Null name.");
        }

        if (Objects.isNull(password)) {
            throw new IllegalArgumentException("Null password.");
        }
    }

}
