package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.authentication.password;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class PasswordHasher {

    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void main(String[] args) {
        String myPassword = "password";
        String hashedPassword = PasswordHasher.hashPassword(myPassword);
        System.out.println(hashedPassword);
    }
}
