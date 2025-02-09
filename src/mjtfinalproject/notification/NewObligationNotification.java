package mjtfinalproject.notification;

import mjtfinalproject.obligation.Obligation;

import java.util.Objects;

public class NewObligationNotification implements Notification {

    private final Obligation obligation;

    public NewObligationNotification(Obligation obligation) {
        if (Objects.isNull(obligation)) {
            throw new IllegalArgumentException("Null obligation.");
        }

        this.obligation = obligation;
    }

    @Override
    public String toString() {
        return obligation.toString();
    }
}
