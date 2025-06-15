package mjt.project.notification;

import mjt.project.entities.users.RegisteredUser;

import java.util.Objects;

public class ApprovedPaymentNotification implements Notification {

    private final String user;
    private final double amount;

    public ApprovedPaymentNotification(RegisteredUser user, double amount) {
        validateArguments(user, amount);

        this.user = user.getUsername();
        this.amount = amount;
    }

    @Override
    public String toString() {
        return user + " approved your payment for " + amount + "LV." + System.lineSeparator();
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
