package mjtfinalproject.obligation;

import mjtfinalproject.entities.users.RegisteredUser;

public record Obligation(RegisteredUser receiver, double amount, String reason) {
    @Override
    public String toString() {
        return "You owe " + receiver.getUsername() + " " + amount + "LV [" + reason + " ].";
    }
}