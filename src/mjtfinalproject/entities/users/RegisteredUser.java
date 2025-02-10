package mjtfinalproject.entities.users;

import mjtfinalproject.entities.group.Group;
import mjtfinalproject.obligation.Obligation;

import java.io.Serializable;
import java.util.UUID;

public interface RegisteredUser extends Serializable {
    void addFriend(RegisteredUser friendToAdd);

    void addGroup(Group group);

    boolean isFriend(RegisteredUser user);

    UUID getGroup(String groupName);

    boolean isGroupNameUnique(String name);

    String getUsername();

    int getPassword();

    String getNewNotifications();

    String getStatus();

    void addNewObligationToFriend(Obligation obligation);

    void addNewObligationInGroup(Group group, Obligation obligation);

    void removeObligationInGroup(Group group, RegisteredUser user, double amount);

    void removeObligationToFriend(RegisteredUser user, double amount);

    boolean markAsPayedFromFriend(RegisteredUser user, double amount);

    void addNewWaitingPaymentFromFriend(RegisteredUser user, double amount);

    void addNewWaitingPaymentFromGroupMember(Group group, RegisteredUser user, double amount);

    boolean markAsPayedFromGroupMember(Group group, RegisteredUser user, double amount);
}
