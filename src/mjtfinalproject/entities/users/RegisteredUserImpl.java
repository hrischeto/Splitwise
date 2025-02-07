package mjtfinalproject.entities.users;

import mjtfinalproject.command.commands.profilemanagement.passwordencryptor.PasswordEncryptor;
import mjtfinalproject.entities.group.Group;
import mjtfinalproject.exceptions.InvalidEntity;
import mjtfinalproject.exceptions.InvalidObligationException;
import mjtfinalproject.notification.ApprovedPaymentNotification;
import mjtfinalproject.notification.NewObligationNotification;
import mjtfinalproject.notification.Notification;
import mjtfinalproject.obligation.Obligation;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RegisteredUserImpl implements RegisteredUser {

    private static final double ALLOWANCE = 0.000001;
    private final String name;
    private final int password;

    private final Set<Notification> newNotifications;
    private final Map<RegisteredUser, Double> obligationsToPay;
    private final Map<RegisteredUser, Double> paymentsToReceive;

    private final Set<RegisteredUser> friends;
    private final Set<Group> groups;

    public RegisteredUserImpl(String name, String password) {
        validateArguments(name, password);

        this.name = name;
        this.password = PasswordEncryptor.encryptPassword(password);

        newNotifications = Collections.newSetFromMap(new ConcurrentHashMap<>());
        obligationsToPay = new ConcurrentHashMap<>();
        paymentsToReceive = new ConcurrentHashMap<>();

        friends = new HashSet<>();
        groups = new HashSet<>();
    }

    @Override
    public void addFriend(RegisteredUser friendToAdd) {
        validateFriend(friendToAdd);

        friends.add(friendToAdd);
    }

    @Override
    public void addGroup(Group group) {
        if (Objects.isNull(group)) {
            throw new InvalidEntity("Group to join was null.");
        }

        groups.add(group);
    }

    @Override
    public boolean isFriend(RegisteredUser user) {
        if (Objects.isNull(user)) {
            throw new InvalidEntity("Null user.");
        }

        return friends.contains(user);
    }

    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public int getPassword() {
        return password;
    }

    @Override
    public Set<Notification> getNewNotifications() {
        return Collections.unmodifiableSet(newNotifications);
    }

    @Override
    public void deleteNotifications() {
        newNotifications.clear();
    }

    @Override
    public void addNewObligation(Obligation obligation) {
        if (Objects.isNull(obligation)) {
            throw new IllegalArgumentException("Null obligation.");
        }

        double amount = obligationsToPay.getOrDefault(obligation.receiver(), 0.0);
        obligationsToPay.put(obligation.receiver(), amount + obligation.amount());

        newNotifications.add(new NewObligationNotification(obligation));
    }

    @Override
    public void removeObligation(RegisteredUser user, double amount) {
        validatePayment(user, amount);
        if (!obligationsToPay.containsKey(user) || obligationsToPay.get(user) == 0.0) {
            throw new InvalidObligationException("No obligations for this user.");
        }

        double currentAmount = obligationsToPay.get(user);
        if (currentAmount - amount < 0 || Math.abs(currentAmount - amount) < ALLOWANCE) {
            obligationsToPay.remove(user);
        } else {
            obligationsToPay.replace(user, currentAmount - amount);
        }

        newNotifications.add(new ApprovedPaymentNotification(user, amount));
    }

    @Override
    public void addNewWaitingPayment(RegisteredUser user, double amount) {
        validatePayment(user, amount);

        double current = paymentsToReceive.getOrDefault(user, 0.0);
        paymentsToReceive.put(user, current + amount);
    }

    private void validatePayment(RegisteredUser user, double amount) {
        if (Objects.isNull(user)) {
            throw new IllegalArgumentException("Null user.");
        }

        if (amount < 0.0) {
            throw new InvalidObligationException("Amount should be non negative.");
        }
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
        return Objects.equals(name, that.getUsername());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

}
