package mjt.project.command.commands.profilemanagement.passwordhasher;

import mjt.project.entities.Password;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PasswordHasherTest {

    @Test
    void testNullInput() {
        assertThrows(IllegalArgumentException.class, () -> PasswordHasher.hashPassword(null),
            "An exception should be thrown when passed a null input.");
    }

    @Test
    void testCompareSamePasswords() {
        Password password = PasswordHasher.hashPassword("pizza");

        assertTrue(PasswordHasher.comparePasswords("pizza", password),
            "Comparing same passwords should return true.");
    }

    @Test
    void testCompareDifferentPasswords() {
        Password password = PasswordHasher.hashPassword("pizza");

        assertFalse(PasswordHasher.comparePasswords("differentPassword", password),
            "Comparing different passwords should return false.");
    }

}
