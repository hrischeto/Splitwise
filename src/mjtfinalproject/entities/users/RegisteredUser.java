package mjtfinalproject.entities.users;

import mjtfinalproject.command.commands.profilemanagement.passwordencryptor.PasswordEncryptor;
import mjtfinalproject.entities.group.Group;
import mjtfinalproject.exceptions.InvalidEntity;
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
        this.password = PasswordEncryptor.encryptPassword(password);

        obligationsToPay = new HashSet<>();
        paymentsToReceive = new HashSet<>();

        friends = new HashSet<>();
        groups = new HashSet<>();
    }

    public void addFriend(RegisteredUser friendToAdd) {
        validateFriend(friendToAdd);

        friends.add(friendToAdd);
    }

    public void addToGroup(Group group) {
        if (Objects.isNull(group)) {
            throw new InvalidEntity("Group to join was null.");
        }

        groups.add(group);
    }

    public boolean isFriend(RegisteredUser user) {
        if (Objects.isNull(user)) {
            throw new InvalidEntity("Null user.");
        }

        return friends.contains(user);
    }

    public String getUsername() {
        return name;
    }

    public int getPassword() {
        return password;
    }

    private void validateArguments(String name, String password) {
        if (Objects.isNull(name)) {
            throw new IllegalArgumentException("Null name.");
        }

        if (Objects.isNull(password)) {
            throw new IllegalArgumentException("Null password.");
        }
    }

    private void validateFriend(RegisteredUser friendToAdd) {
        if (Objects.isNull(friendToAdd)) {
            throw new InvalidEntity("User to add as friend was null");
        }

        if (friendToAdd.equals(this)) {
            throw new InvalidEntity("Cannot add yourself as a friend");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegisteredUser that = (RegisteredUser) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

}
