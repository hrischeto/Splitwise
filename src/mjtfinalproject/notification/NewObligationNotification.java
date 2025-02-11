package mjtfinalproject.notification;

import mjtfinalproject.obligation.Obligation;

import java.util.Objects;

public class NewObligationNotification implements Notification {

    private final String receiver;
    private final double amount;
    private final String reason;

    public NewObligationNotification(Obligation obligation) {
        if (Objects.isNull(obligation)) {
            throw new IllegalArgumentException("Null obligation.");
        }

        this.receiver = obligation.receiver();
        this.amount = obligation.amount();
        this.reason = obligation.reason();
    }

    @Override
    public String toString() {
        return "You owe " + receiver + " " + amount + "LV [" + reason + "]." + System.lineSeparator();
    }
}
