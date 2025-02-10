package mjtfinalproject.notification;

import mjtfinalproject.entities.users.RegisteredUser;

import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.util.Objects;

public class ApprovedPaymentNotification implements Notification {

    @Serial
    private static final long serialVersionUID = 1234L;

    private final String user;
    private final double amount;

    public ApprovedPaymentNotification(RegisteredUser user, double amount) {
        validateArguments(user, amount);

        this.user = user.getUsername();
        this.amount = amount;
    }

    @Override
    public String toString() {
        return user + " approved your payment for " + amount + "LV.\n";
    }

    @Serial
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    @Serial
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        if (in.readLong() != serialVersionUID ) {
            throw new InvalidClassException("Unexpected serialVersionUID");
        }

        in.defaultReadObject();
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
