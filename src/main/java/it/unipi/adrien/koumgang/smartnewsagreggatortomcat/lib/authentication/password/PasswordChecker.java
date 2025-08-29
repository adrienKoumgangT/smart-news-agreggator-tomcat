package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.authentication.password;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class PasswordChecker {

    public static boolean checkPassword(String storedPassword, String enteredPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(enteredPassword.getBytes(StandardCharsets.UTF_8));
            String enteredHashedPassword = Base64.getEncoder().encodeToString(hash);
            return storedPassword.equals(enteredHashedPassword);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
