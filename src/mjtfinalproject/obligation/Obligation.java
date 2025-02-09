package mjtfinalproject.obligation;

import java.io.Serializable;

public record Obligation(String receiver, double amount, String reason) implements Serializable {
    @Override
    public String toString() {
        return "You owe " + receiver + " " + amount + "LV [" + reason + " ].";
    }
}