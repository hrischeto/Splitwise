package mjtfinalproject.obligation;

import mjtfinalproject.entities.users.RegisteredUser;

public record Obligation(RegisteredUser receiver, double amount) {
}
