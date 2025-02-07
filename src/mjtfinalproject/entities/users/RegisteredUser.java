package mjtfinalproject.entities.users;

import mjtfinalproject.entities.group.Group;
import mjtfinalproject.notification.Notification;
import mjtfinalproject.obligation.Obligation;

import java.util.Set;
import java.util.UUID;

public interface RegisteredUser {
    void addFriend(RegisteredUser friendToAdd);

    void addGroup(Group group);

    boolean isFriend(RegisteredUser user);

    UUID getGroup(String groupName);

    boolean isGroupNameUnique(String name);

    String getUsername();

    int getPassword();

    Set<Notification> getNewNotifications();

    void deleteNotifications();

    void addNewObligation(Obligation obligation);

    void removeObligation(RegisteredUser user, double amount);

    void addNewWaitingPayment(RegisteredUser user, double amount);
}
