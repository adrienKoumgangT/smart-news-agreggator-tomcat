package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.authentication.token;

import com.google.gson.GsonBuilder;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.authentication.user.UserToken;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.configuration.service.ApiConfiguration;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.exception.safe.InvalidTokenException;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class TokenManager {
    // Load from env or config
    private static final String SECRET;

    static {
        try {
            SECRET = (new ApiConfiguration()).getJwsKey();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    // private static final SecretKey key = Jwts.SIG.HS512.key().build();
    // private static final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    private static final SecretKey key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET));
    private static final Long TOKEN_EXPIRATION_TIME = 12 * 60 * 60 * 1000L; // 12h

    public static String createJWTAndSign(UserToken user) {
        try {
            ApiConfiguration apiConfiguration = new ApiConfiguration();

            Instant now = Instant.now();

            return Jwts.builder()
                    .issuer(apiConfiguration.getBaseApiUrl())
                    .subject(user.getIdUser())
                    .claim("user", user.toMap())
                    .notBefore(Date.from(now))
                    .expiration(Date.from(now.plus(TOKEN_EXPIRATION_TIME, ChronoUnit.MILLIS)))
                    // .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                    .signWith(key)
                    .issuedAt(Date.from(now))
                    .id(UUID.randomUUID().toString())
                    .compact()
                    ;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static UserToken readToken(String token) throws Exception {
        try {
            // On décode le jeton en utilisant la clé secrète
            Claims claims = Jwts.parser()
                    .verifyWith(key).build()
                    .parseSignedClaims(token)
                    .getPayload();

            HashMap hm = (HashMap) claims.get("user");
            String data = new GsonBuilder().serializeNulls().create().toJson(hm);

            // Type type = new TypeToken<UserToken>() {}.getType();
            return new GsonBuilder().serializeNulls().create().fromJson(data, UserToken.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidTokenException("Token Expired");
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("===> Read private key");

        System.out.println("===> Create user");
        UserToken user = new UserToken();
        user.setIdUser("1L");
        user.setUsername("adrientkoumgang@gmail.com");
        user.setFirstname("adrien");
        user.setLastname("koumgang tegantchouang");
        user.setAdmin(true);
        user.setStatus("active");
        System.out.println(user);

        System.out.println("===> Create token");
        String token = createJWTAndSign(user);
        System.out.println("Token lu = " + token);

        if(token != null) {
            System.out.println("===> Read token");
            UserToken user_read = readToken(token);
            System.out.println("user lu = " + user_read);
        }
    }

}