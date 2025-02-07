package mjtfinalproject.notification;

import mjtfinalproject.entities.users.RegisteredUser;

import java.util.Objects;

public class ApprovedPaymentNotification implements Notification {

    private final RegisteredUser user;
    private final double amount;

    public ApprovedPaymentNotification(RegisteredUser user, double amount) {
        validateArguments(user, amount);

        this.user = user;
        this.amount = amount;
    }

    @Override
    public String getNotification() {
        return user.getUsername() + " approved your payment for " + amount + "LV.";
    }

    private void validateArguments(RegisteredUser user, double amount) {
        if (Objects.isNull(user)) {
            throw new IllegalArgumentException("Null user.");
        }
        if (amount < 0.0) {
            throw new IllegalArgumentException("Amount should not be negative.");
        }
    }
}
