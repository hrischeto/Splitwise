package mjtfinalproject.command.commands.profilemanagement.passwordencryptor;

import java.util.Objects;

public class PasswordEncryptor {

    public static int encryptPassword(String passwordString) {
        if (Objects.isNull(passwordString)) {
            throw new IllegalArgumentException("Provided password string was null.");
        }
        return passwordString.hashCode();
    }

}
