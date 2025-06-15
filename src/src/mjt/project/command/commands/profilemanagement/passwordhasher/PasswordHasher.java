package mjt.project.command.commands.profilemanagement.passwordhasher;

import mjt.project.entities.Password;
import mjt.project.exceptions.FailedPasswordHashingException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Objects;

public class PasswordHasher {

    private static final int SALT_LENGTH = 16;
    private static final int ITERATIONS = 1000;
    private static final int KEY_LENGTH = 256;
    private static final String HASHING_ALGORITHM = "PBKDF2WithHmacSHA256";

    public static Password hashPassword(String passwordString) {
        if (Objects.isNull(passwordString)) {
            throw new IllegalArgumentException("Provided password string was null.");
        }

        byte[] salt = generateSalt();
        PBEKeySpec spec = new PBEKeySpec(passwordString.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        byte[] hash;

        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance(HASHING_ALGORITHM);
            hash = skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new FailedPasswordHashingException(e.getMessage(), e);
        }

        return new Password(Base64.getEncoder().encodeToString(hash), salt);
    }

    public static boolean comparePasswords(String toCheck, Password password) {
        String passwordToCheck = hashPassword(toCheck, password.salt());

        return password.password().equals(passwordToCheck);
    }

    private static String hashPassword(String passwordString, byte[] salt) {
        if (Objects.isNull(passwordString)) {
            throw new IllegalArgumentException("Provided password string was null.");
        }
        if (Objects.isNull(salt)) {
            throw new IllegalArgumentException("Provided salt was null.");
        }

        PBEKeySpec spec = new PBEKeySpec(passwordString.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        byte[] hash;

        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance(HASHING_ALGORITHM);
            hash = skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new FailedPasswordHashingException(e.getMessage(), e);
        }

        return Base64.getEncoder().encodeToString(hash);
    }

    private static byte[] generateSalt() {
        SecureRandom sr = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        sr.nextBytes(salt);
        return salt;
    }

}
