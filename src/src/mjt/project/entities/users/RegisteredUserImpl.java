package mjt.project.entities.users;

import mjt.project.command.commands.profilemanagement.passwordhasher.PasswordHasher;
import mjt.project.currencymanagment.CurrencyManager;
import mjt.project.entities.Password;
import mjt.project.entities.group.Group;
import mjt.project.exceptions.InvalidEntity;
import mjt.project.exceptions.InvalidObligationException;
import mjt.project.notification.ApprovedPaymentNotification;
import mjt.project.notification.NewObligationNotification;
import mjt.project.notification.Notification;
import mjt.project.obligation.Obligation;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RegisteredUserImpl implements RegisteredUser {

    private static final double ALLOWANCE = 0.000001;

    private final String name;
    private final Password password;
    private double currentCurrencyRate;

    private final Set<Notification> newNotificationsFromFriends;
    private final Map<String, Set<Notification>> newNotificationsFromGroups;

    private final Map<String, Double> obligationsToPay;
    private final Map<String, Double> paymentsToReceive;

    private final Map<String, Map<String, Double>> obligationsToPayInGroups;
    private final Map<String, Map<String, Double>> paymentsToReceiveInGroups;

    private final Set<String> friends;
    private final Map<String, UUID> groups;

    public RegisteredUserImpl(String name, String password) {
        validateArguments(name, password);

        this.name = name;
        this.password = PasswordHasher.hashPassword(password);
        currentCurrencyRate = CurrencyManager.DEFAULT_CURRENCY_RATE;

        newNotificationsFromFriends = Collections.newSetFromMap(new ConcurrentHashMap<>());
        newNotificationsFromGroups = new ConcurrentHashMap<>();

        obligationsToPay = new ConcurrentHashMap<>();
        paymentsToReceive = new ConcurrentHashMap<>();

        obligationsToPayInGroups = new ConcurrentHashMap<>();
        paymentsToReceiveInGroups = new ConcurrentHashMap<>();

        friends = Collections.newSetFromMap(new ConcurrentHashMap<>());
        groups = new ConcurrentHashMap<>();
    }

    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public Password getPassword() {
        return password;
    }

    @Override
    public UUID getGroup(String groupName) {
        if (Objects.isNull(groupName)) {
            throw new IllegalArgumentException("Null group name.");
        }

        return groups.get(groupName);
    }

    @Override
    public void addFriend(RegisteredUser friendToAdd) {
        validateFriend(friendToAdd);

        friends.add(friendToAdd.getUsername());
    }

    @Override
    public void addGroup(Group group) {
        if (Objects.isNull(group)) {
            throw new InvalidEntity("Group to join was null.");
        }

        UUID existing = groups.putIfAbsent(group.getName(), group.id());
        if (Objects.nonNull(existing)) {
            throw new InvalidEntity("User is already in a group with that name.");
        }
    }

    @Override
    public boolean isGroupNameUnique(String name) {
        if (Objects.isNull(name)) {
            throw new IllegalArgumentException("null name");
        }

        return !groups.containsKey(name);
    }

    @Override
    public boolean isFriend(RegisteredUser user) {
        if (Objects.isNull(user)) {
            throw new InvalidEntity("Null user.");
        }

        return friends.contains(user.getUsername());
    }

    @Override
    public void addNewObligationToFriend(Obligation obligation) {
        if (Objects.isNull(obligation)) {
            throw new IllegalArgumentException("Null obligation.");
        }

        synchronized (this) {
            double amount = obligationsToPay.getOrDefault(obligation.receiver(), 0.0);
            obligationsToPay.put(obligation.receiver(), amount + obligation.amount());
        }

        newNotificationsFromFriends.add(new NewObligationNotification(obligation));
    }

    @Override
    public void removeObligationToFriend(RegisteredUser user, double amount) {
        validatePayment(user, amount);

        synchronized (this) {
            if (!obligationsToPay.containsKey(user.getUsername()) || obligationsToPay.get(user.getUsername()) == 0.0) {
                throw new InvalidObligationException("No obligations for this user.");
            }

            double currentAmount = obligationsToPay.get(user.getUsername());
            if (currentAmount - amount < 0 || Math.abs(currentAmount - amount) < ALLOWANCE) {
                obligationsToPay.remove(user.getUsername());
            } else {
                obligationsToPay.replace(user.getUsername(), currentAmount - amount);
            }
        }
        newNotificationsFromFriends.add(new ApprovedPaymentNotification(user, amount));
    }

    @Override
    public boolean markAsPayedFromFriend(RegisteredUser user, double amount) {
        validatePayment(user, amount);

        synchronized (this) {
            if (!paymentsToReceive.containsKey(user.getUsername()) ||
                paymentsToReceive.get(user.getUsername()) == 0.0) {
                return false;
            }

            double currentAmount = paymentsToReceive.get(user.getUsername());
            if (currentAmount - amount < 0 || Math.abs(currentAmount - amount) < ALLOWANCE) {
                paymentsToReceive.remove(user.getUsername());
            } else {
                paymentsToReceive.replace(user.getUsername(), currentAmount - amount);
            }
        }

        return true;
    }

    @Override
    public void addNewWaitingPaymentFromFriend(RegisteredUser user, double amount) {
        validatePayment(user, amount);

        synchronized (this) {
            double current = paymentsToReceive.getOrDefault(user.getUsername(), 0.0);
            paymentsToReceive.put(user.getUsername(), current + amount);
        }
    }

    @Override
    public void addNewObligationInGroup(Group group, Obligation obligation) {
        if (Objects.isNull(obligation)) {
            throw new IllegalArgumentException("Null obligation.");
        }
        if (Objects.isNull(group)) {
            throw new IllegalArgumentException("Null group.");
        }

        synchronized (this) {
            if (!obligationsToPayInGroups.containsKey(group.getName())) {
                obligationsToPayInGroups.put(group.getName(), new HashMap<>());
            }

            double amount = obligationsToPayInGroups.get(group.getName()).getOrDefault(obligation.receiver(), 0.0);
            obligationsToPayInGroups.get(group.getName()).put(obligation.receiver(), amount + obligation.amount());

            addNewObligationNotification(group, obligation);
        }
    }

    private void addNewObligationNotification(Group group, Obligation obligation) {
        if (Objects.isNull(obligation)) {
            throw new IllegalArgumentException("Null obligation.");
        }
        if (Objects.isNull(group)) {
            throw new IllegalArgumentException("Null group.");
        }

        synchronized (this) {
            if (!newNotificationsFromGroups.containsKey(group.getName())) {
                newNotificationsFromGroups.put(group.getName(), new HashSet<>());
            }

            newNotificationsFromGroups.get(group.getName()).add(new NewObligationNotification(obligation));
        }
    }

    @Override
    public void removeObligationInGroup(Group group, RegisteredUser user, double amount) {
        validateGroupPayment(group, user, amount);

        synchronized (this) {
            if (!obligationsToPayInGroups.containsKey(group.getName()) ||
                obligationsToPayInGroups.get(group.getName()).isEmpty()) {
                throw new InvalidObligationException("No obligations for this group.");
            }

            if (!obligationsToPayInGroups.get(group.getName()).containsKey(user.getUsername()) ||
                obligationsToPayInGroups.get(group.getName()).get(user.getUsername()) == 0.0) {
                throw new InvalidObligationException("No obligations for this user.");
            }

            double currentAmount = obligationsToPayInGroups.get(group.getName()).get(user.getUsername());
            if (currentAmount - amount < 0 || Math.abs(currentAmount - amount) < ALLOWANCE) {
                obligationsToPayInGroups.get(group.getName()).remove(user.getUsername());
            } else {
                obligationsToPayInGroups.get(group.getName()).replace(user.getUsername(), currentAmount - amount);
            }

            addNewApprovedPaymentNotification(group, user, amount);
        }
    }

    private synchronized void addNewApprovedPaymentNotification(Group group, RegisteredUser user, double amount) {
        if (!newNotificationsFromGroups.containsKey(group.getName())) {
            newNotificationsFromGroups.put(group.getName(), new HashSet<>());
        }

        newNotificationsFromGroups.get(group.getName()).add(new ApprovedPaymentNotification(user, amount));
    }

    @Override
    public boolean markAsPayedFromGroupMember(Group group, RegisteredUser user, double amount) {
        validateGroupPayment(group, user, amount);

        synchronized (this) {
            if (!paymentsToReceiveInGroups.containsKey(group.getName()) ||
                paymentsToReceiveInGroups.get(group.getName()).isEmpty()) {
                return false;
            }

            if (!paymentsToReceiveInGroups.get(group.getName()).containsKey(user.getUsername()) ||
                paymentsToReceiveInGroups.get(group.getName()).get(user.getUsername()) == 0.0) {
                return false;
            }

            double currentAmount = paymentsToReceiveInGroups.get(group.getName()).get(user.getUsername());
            if (currentAmount - amount < 0 || Math.abs(currentAmount - amount) < ALLOWANCE) {
                paymentsToReceiveInGroups.get(group.getName()).remove(user.getUsername());
            } else {
                paymentsToReceiveInGroups.get(group.getName()).replace(user.getUsername(), currentAmount - amount);
            }
        }

        return true;
    }

    @Override
    public void addNewWaitingPaymentFromGroupMember(Group group, RegisteredUser user, double amount) {
        validateGroupPayment(group, user, amount);

        synchronized (this) {
            if (!paymentsToReceiveInGroups.containsKey(group.getName())) {
                paymentsToReceiveInGroups.put(group.getName(), new HashMap<>());
            }

            double current = paymentsToReceiveInGroups.get(group.getName()).getOrDefault(user.getUsername(), 0.0);
            paymentsToReceiveInGroups.get(group.getName()).put(user.getUsername(), current + amount);
        }
    }

    @Override
    public String getNewNotifications() {
        if (newNotificationsFromFriends.isEmpty() && newNotificationsFromGroups.isEmpty()) {
            return "No new notifications";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(System.lineSeparator()).append("-Notifications-").append(System.lineSeparator());

        sb.append("*Friends:").append(System.lineSeparator());
        newNotificationsFromFriends.forEach(notification -> sb.append(notification.toString()));

        sb.append("*Groups:").append(System.lineSeparator());
        newNotificationsFromGroups.keySet().forEach(key -> {
            sb.append(key).append(":").append(System.lineSeparator());
            newNotificationsFromGroups.get(key).forEach(notification -> sb.append(notification.toString()));
        });

        newNotificationsFromFriends.clear();
        newNotificationsFromGroups.clear();
        return sb.toString();
    }

    @Override
    public String getStatus() {
        if (obligationsToPay.isEmpty() && paymentsToReceive.isEmpty() && obligationsToPayInGroups.isEmpty() &&
            paymentsToReceiveInGroups.isEmpty()) {
            return "Noting to show.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(System.lineSeparator()).append("-Status-").append(System.lineSeparator());

        sb.append("*Friends:").append(System.lineSeparator());
        obligationsToPay.keySet()
            .forEach(key -> sb.append(generateObligationSentence(key)));
        paymentsToReceive.keySet()
            .forEach(key -> sb.append(generateWaitingPaymentSentence(key)));

        sb.append("*Groups:").append(System.lineSeparator());
        sb.append(generateGroupObligationSentence());
        sb.append(generateAnticipatedGroupPayments());

        return sb.toString();
    }

    @Override
    public double getCurrentCurrencyRate() {
        return currentCurrencyRate;
    }

    @Override
    public void setCurrentCurrencyRate(double currentCurrencyRate) {
        if (currentCurrencyRate <= 0) {
            throw new IllegalArgumentException("Rate must be positive");
        }
        this.currentCurrencyRate = currentCurrencyRate;
    }

    private String generateObligationSentence(String username) {
        return "You owe " + username + " " + obligationsToPay.get(username) + "LV." + System.lineSeparator();
    }

    private String generateWaitingPaymentSentence(String username) {
        return username + " ows you " + paymentsToReceive.get(username) + "LV." + System.lineSeparator();
    }

    private String generateGroupObligationSentence(String username, String group) {
        return "You owe " + username + " " + obligationsToPayInGroups.get(group).get(username) + "LV." +
            System.lineSeparator();
    }

    private String generateGroupWaitingPaymentSentence(String username, String group) {
        return username + " ows you " + paymentsToReceiveInGroups.get(group).get(username) + "LV." +
            System.lineSeparator();
    }

    private String generateGroupObligationSentence() {
        StringBuilder sb = new StringBuilder();
        obligationsToPayInGroups.keySet().forEach(key -> {
            sb.append(key).append(":").append(System.lineSeparator());
            obligationsToPayInGroups.get(key).keySet()
                .forEach(groupMember -> sb.append(generateGroupObligationSentence(groupMember, key)));
        });
        return sb.toString();
    }

    private String generateAnticipatedGroupPayments() {
        StringBuilder sb = new StringBuilder();
        paymentsToReceiveInGroups.keySet().forEach(key -> {
            sb.append(key).append(":").append(System.lineSeparator());
            paymentsToReceiveInGroups.get(key).keySet()
                .forEach(groupMember -> sb.append(generateGroupWaitingPaymentSentence(groupMember, key)));
        });
        return sb.toString();
    }

    private void validatePayment(RegisteredUser user, double amount) {
        if (Objects.isNull(user)) {
            throw new IllegalArgumentException("Null user.");
        }

        if (amount < 0.0) {
            throw new InvalidObligationException("Amount should be non negative.");
        }
    }

    private void validateGroupPayment(Group group, RegisteredUser user, double amount) {
        if (Objects.isNull(group)) {
            throw new IllegalArgumentException("Null group.");
        }
        if (Objects.isNull(user)) {
            throw new IllegalArgumentException("Null user");
        }
        if (amount < 0.0) {
            throw new InvalidObligationException("Negative amount.");
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
