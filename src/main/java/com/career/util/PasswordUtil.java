package com.career.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Utility class for secure password hashing and verification using
 * PBKDF2WithHmacSHA256 with cryptographically strong salt and iteration count.
 * Never stores or compares plaintext passwords.
 */
public class PasswordUtil {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256; // 32 bytes
    private static final int SALT_LENGTH = 16;  // 16 bytes

    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Hashes a plaintext password using PBKDF2WithHmacSHA256.
     * 
     * @param password Plaintext password to hash
     * @return Formatted string: "iterations:saltHex:hashHex"
     */
    public static String hashPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        byte[] salt = new byte[SALT_LENGTH];
        RANDOM.nextBytes(salt);

        byte[] hash = pbkdf2(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);

        return ITERATIONS + ":" + toHex(salt) + ":" + toHex(hash);
    }

    /**
     * Verifies a candidate password against a stored hashed password string.
     * 
     * @param candidatePassword The plaintext password entered by the user
     * @param storedHash The hashed string previously generated (iterations:saltHex:hashHex)
     * @return true if password matches, false otherwise
     */
    public static boolean verifyPassword(String candidatePassword, String storedHash) {
        if (candidatePassword == null || storedHash == null) {
            return false;
        }

        String[] parts = storedHash.split(":");
        if (parts.length != 3) {
            return false;
        }

        try {
            int iterations = Integer.parseInt(parts[0]);
            byte[] salt = fromHex(parts[1]);
            byte[] expectedHash = fromHex(parts[2]);

            byte[] actualHash = pbkdf2(candidatePassword.toCharArray(), salt, iterations, expectedHash.length * 8);

            return slowEquals(expectedHash, actualHash);
        } catch (Exception e) {
            return false;
        }
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLength) {
        try {
            KeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            return factory.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error while computing PBKDF2 hash", e);
        }
    }

    /**
     * Constant-time byte array comparison to prevent timing attack vulnerabilities.
     */
    private static boolean slowEquals(byte[] a, byte[] b) {
        if (a == null || b == null) {
            return false;
        }
        int diff = a.length ^ b.length;
        for (int i = 0; i < a.length && i < b.length; i++) {
            diff |= a[i] ^ b[i];
        }
        return diff == 0;
    }

    private static String toHex(byte[] array) {
        StringBuilder sb = new StringBuilder(array.length * 2);
        for (byte b : array) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private static byte[] fromHex(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                                 + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }
}
