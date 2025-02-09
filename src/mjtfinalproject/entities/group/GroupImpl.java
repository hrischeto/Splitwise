package mjtfinalproject.entities.group;

import mjtfinalproject.entities.users.RegisteredUser;
import mjtfinalproject.obligation.Obligation;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GroupImpl implements Group {

    private final UUID id;
    private final String name;
    private final Set<RegisteredUser> members;

    public GroupImpl(String name, Set<RegisteredUser> members) {
        validateArguments(name, members);

        this.id = UUID.randomUUID();
        this.name = name;

        this.members = Collections.newSetFromMap(new ConcurrentHashMap<>());
        this.members.addAll(members);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Set<RegisteredUser> getMembers() {
        return Collections.unmodifiableSet(members);
    }

    @Override
    public UUID id() {
        return id;
    }

    @Override
    public void splitAmount(double amount, RegisteredUser payingUser, String reason) {
        validatePaymentDetails(amount, payingUser, reason);

        double amountToPay = amount / members.size();
        for (RegisteredUser member : members) {
            if (members.equals(payingUser)) {
                continue;
            }
            member.addNewObligationInGroup(this, new Obligation(payingUser.getUsername(), amountToPay, reason));
            payingUser.addNewWaitingPaymentFromGroupMember(this, member, amountToPay);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupImpl group = (GroupImpl) o;
        return Objects.equals(id, group.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    private void validateArguments(String name, Set<RegisteredUser> members) {
        if (Objects.isNull(name)) {
            throw new IllegalArgumentException("Group name was null.");
        }

        if (Objects.isNull(members)) {
            throw new IllegalArgumentException("Set of members was null.");
        }
    }

    private void validatePaymentDetails(double amount, RegisteredUser payingUser, String reason) {
        if (amount < 0.0) {
            throw new IllegalArgumentException("Amount to split should be positive.");
        }

        if (Objects.isNull(payingUser)) {
            throw new IllegalArgumentException("Null paying user.");
        }

        if (Objects.isNull(reason)) {
            throw new IllegalArgumentException("Null reason.");
        }
    }
}