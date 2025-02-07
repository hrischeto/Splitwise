package mjtfinalproject.entities.group;

import mjtfinalproject.entities.users.RegisteredUser;
import mjtfinalproject.obligation.Obligation;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Group {

    private final UUID id;
    private final String name;
    private final Set<RegisteredUser> members;

    public Group(String name, Set<RegisteredUser> members) {
        validateArguments(name, members);

        this.id = UUID.randomUUID();
        this.name = name;

        this.members = Collections.newSetFromMap(new ConcurrentHashMap<>());
        this.members.addAll(members);
    }

    public String getName() {
        return name;
    }

    public Set<RegisteredUser> getMembers() {
        return Collections.unmodifiableSet(members);
    }

    public UUID id() {
        return id;
    }

    public void splitAmount(double amount, RegisteredUser payingUser, String reason) {
        validatePaymentDetails(amount, payingUser, reason);

        double amountToPay = amount / members.size();
        for (RegisteredUser member : members) {
            member.addNewObligation(new Obligation(payingUser, amountToPay, reason));
            payingUser.addNewWaitingPayment(member, amountToPay);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
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