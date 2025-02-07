package mjtfinalproject.entities.users;

import mjtfinalproject.entities.group.Group;
import mjtfinalproject.notification.Notification;
import mjtfinalproject.obligation.Obligation;

import java.util.Set;

public interface RegisteredUser {
    void addFriend(RegisteredUser friendToAdd);

    void addGroup(Group group);

    boolean isFriend(RegisteredUser user);

    String getUsername();

    int getPassword();

    Set<Notification> getNewNotifications();

    void deleteNotifications();

    void addNewObligation(Obligation obligation);

    void removeObligation(RegisteredUser user, double amount);

    void addNewWaitingPayment(RegisteredUser user, double amount);
}
