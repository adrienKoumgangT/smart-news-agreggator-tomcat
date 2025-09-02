package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.authentication.generator;

import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import java.util.Base64;

public class KeyGenerator {

    public static void main(String[] args) {
        // Generate a new HS512 key
        SecretKey key = Jwts.SIG.HS512.key().build();

        // Encode it to Base64 so you can save in config or env
        String base64Key = Base64.getEncoder().encodeToString(key.getEncoded());

        System.out.println("Your JWT secret key (Base64): " + base64Key);
    }

}
