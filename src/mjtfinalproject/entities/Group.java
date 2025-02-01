package mjtfinalproject.entities;

import mjtfinalproject.entities.users.RegisteredUser;

import java.util.Set;
import java.util.UUID;

public record Group(UUID id, String name, Set<RegisteredUser> participants) {

}
