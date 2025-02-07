package mjtfinalproject.command.commands.profilemanagement.passwordencryptor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PasswordEncryptorTest {

    @Test
    void testNullInput() {
        assertThrows(IllegalArgumentException.class, () -> PasswordEncryptor.encryptPassword(null),
            "An exception should be thrown when passed a null input.");
    }

    @Test
    void testCorrectInput() {
        assertEquals(PasswordEncryptor.encryptPassword("password"), "password".hashCode(),
            "Encryptor should encrypt password correctly.");
    }
}
