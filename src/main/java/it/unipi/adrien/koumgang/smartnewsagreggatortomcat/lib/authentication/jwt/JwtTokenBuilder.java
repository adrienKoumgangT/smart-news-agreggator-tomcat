package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.authentication.jwt;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

// import static com.auth0.jwt.impl.PublicClaims;

/**
 * Builder class for simplifying JWT token creation.
 *
 * <tt>userId</tt> and <tt>roles</tt> values are mandatory.
 *
 * @author acomo
 *
 */
public class JwtTokenBuilder {


    private JWTCreator.Builder jwtBuilder = JWT.create();

    private ClaimsAdapter claims = new ClaimsAdapter(jwtBuilder);

    private OptionsAdapter optionsAdapter = new OptionsAdapter(jwtBuilder);

    private Algorithm algorithm;

    private String customUserIdClaim;

    private String customRolesClaim;

    private JwtTokenBuilder() {

    }

    /**
     * Creates a new {@link JwtTokenBuilder} instance
     *
     * @param algorithm to use for encoding
     * @param customUserIdClaim custom user id claim
     * @param customRolesClaim custom roles claim
     *
     * @return a new {@link JwtTokenBuilder} instance
     */
    public static JwtTokenBuilder create(Algorithm algorithm, String customUserIdClaim, String customRolesClaim) {
        JwtTokenBuilder builder = new JwtTokenBuilder();
        builder.algorithm = algorithm;
        builder.optionsAdapter.setIssuedAt(true);
        builder.customUserIdClaim = customUserIdClaim != null ? customUserIdClaim : JwtConstants.USER_ID;
        builder.customRolesClaim = customRolesClaim != null ? customRolesClaim : JwtConstants.ROLES;

        return builder;
    }

    /**
     * Creates a new {@link JwtTokenBuilder} instance
     *
     * @param algorithm to use for encoding
     *
     * @return a new {@link JwtTokenBuilder} instance
     */
    public static JwtTokenBuilder create(Algorithm algorithm) {
        return create(algorithm, JwtConstants.USER_ID, JwtConstants.ROLES);
    }

    /**
     * Creates a {@link JwtTokenBuilder} instance from token and secret.
     * <br >
     * Token <strong>must</strong> contains "<em>iat</em>" param in order to restore builder status
     * <br >
     * Use this method if you want to edit current token: if "<em>jti</em>" param is present, will be overwritten
     * <br >
     * Token <strong>must</strong> be verified before calling this method
     * <br >
     * <br >
     * Rebuilding this token has side effect:
     * <ul>
     * <li>if "<em>jti</em>" param is present, will be overwritten</li>
     * <li>if "<em>exp</em>" param is present, expire time will be recalculated starting from current timestamp</li>
     * <li>if "<em>nbf</em>" param is present, its value will be recalculated starting from current timestamp</li>
     * </ul>
     *
     * @param jwt a {@link JwtAdapter} instance
     *
     * @return a new {@link JwtTokenBuilder} instance
     *
     * @throws IllegalStateException if token is not verified by provided verifier
     */
    public static JwtTokenBuilder from(JwtAdapter jwt) {
        JwtTokenBuilder builder = create(jwt.getAlgorithm(), jwt.getUserIdClaim(), jwt.getRolesClaim());
        DecodedJWT decodedJWT = jwt.getDecodedJWT();
        restoreInternalStatus(builder, decodedJWT);
        return builder;
    }

    /**
     * Creates a {@link JwtTokenBuilder} instance from token and secret.
     * <br >
     * Token <strong>must</strong> contains "<em>iat</em>" param in order to restore builder status
     * <br >
     * Use this method if you want to edit current token: if "<em>jti</em>" param is present, will be overwritten
     * <br >
     * Token <strong>must</strong> be verified before calling this method
     * <br >
     * <br >
     * Rebuilding this token has side effect:
     * <ul>
     * <li>if "<em>jti</em>" param is present, will be overwritten</li>
     * <li>if "<em>exp</em>" param is present, expire time will be recalculated starting from current timestamp</li>
     * <li>if "<em>nbf</em>" param is present, its value will be recalculated starting from current timestamp</li>
     * </ul>
     *
     * @param token a jwt as String
     * @param secret secret text
     * @param customUserIdClaim custom user id claim
     * @param customRolesClaim custom roles claim
     *
     * @return a new {@link JwtTokenBuilder} instance
     *
     * @throws IllegalStateException if token is not verified by provided verifier
     */
    public static JwtTokenBuilder from(String token, String secret, String customUserIdClaim, String customRolesClaim) {
        JwtAdapter jwtAdapter = JwtTokenVerifier.create(secret, customUserIdClaim, customRolesClaim)
                .verify(token);
        return from(jwtAdapter);
    }

    /**
     * See {@link #from(String, String, String, String)}
     *
     * @param token a jwt as String
     * @param secret secret text
     *
     * @return a new {@link JwtTokenBuilder} instance
     */
    public static JwtTokenBuilder from(String token, String secret) {
        return from(token, secret, JwtConstants.USER_ID, JwtConstants.ROLES);
    }

    private static void restoreInternalStatus(JwtTokenBuilder builder, DecodedJWT decodedJWT) {
        Map<String, Claim> verifiedClaims = new HashMap<>(decodedJWT.getClaims());
        if (verifiedClaims.containsKey(PublicClaims.ISSUED_AT)) {
            int issuedAt = verifiedClaims.remove(PublicClaims.ISSUED_AT).asInt();
            if (verifiedClaims.containsKey(PublicClaims.EXPIRES_AT)) {
                int expire = verifiedClaims.remove(PublicClaims.EXPIRES_AT).asInt() - issuedAt;
                builder.optionsAdapter.setExpirySeconds(expire);
            }
            if (verifiedClaims.containsKey(PublicClaims.NOT_BEFORE)) {
                int notBefore = issuedAt - verifiedClaims.remove(PublicClaims.NOT_BEFORE).asInt();
                builder.optionsAdapter.setNotValidBeforeLeeway(notBefore);
            }
            if (verifiedClaims.containsKey(PublicClaims.JWT_ID)) {
                verifiedClaims.remove(PublicClaims.JWT_ID);
                builder.optionsAdapter.setJwtId(true);
            }
            builder.claims.putAll(verifiedClaims);
        } else {
            throw new IllegalStateException("Missing 'iat' value. Unable to restore builder status");
        }
    }

    /**
     * Add <tt>userId</tt> claim to JWT body
     *
     * @param name realm username
     *
     * @return {@link JwtTokenBuilder}
     */
    public JwtTokenBuilder userId(String name) {
        claims.put(customUserIdClaim, name);

        return this;
    }

    /**
     * Add <tt>roles</tt> claim to JWT body
     *
     * @param roles roles to add
     *
     * @return {@link JwtTokenBuilder}
     */
    public JwtTokenBuilder roles(Collection<String> roles) {
        claims.put(customRolesClaim, roles.toArray(new String[]{}));

        return this;
    }

    /**
     * Add a custom claim to JWT body
     *
     * @param key key of new claim
     * @param value value of new claim
     *
     * @return {@link JwtTokenBuilder}
     */
    public JwtTokenBuilder claimEntry(String key, Object value) {
        claims.put(key, value);
        return this;
    }

    /**
     * Add JWT claim <tt>exp</tt> to current timestamp + seconds.
     *
     * @param seconds expires within seconds
     *
     * @return {@link JwtTokenBuilder}
     */
    public JwtTokenBuilder expirySecs(int seconds) {
        optionsAdapter.setExpirySeconds(seconds);
        return this;
    }

    /**
     * Specify algorithm to sign JWT with. Default is HS256.
     *
     * @param algorithm signing algorithm
     *
     * @return {@link JwtTokenBuilder}
     */
    public JwtTokenBuilder algorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
        return this;
    }

    /**
     * Should JWT claim <tt>iat</tt> be added?
     * Value will be set to current timestamp
     *
     * @param issuedAt true to add
     *
     * @return {@link JwtTokenBuilder}
     */
    public JwtTokenBuilder issuedEntry(boolean issuedAt) {
        optionsAdapter.setIssuedAt(issuedAt);
        return this;
    }

    /**
     * Should JWT claim <tt>jti</tt> be added?
     * Value will be set to a pseudo random unique value (UUID)
     *
     * @param jwtId true to add
     *
     * @return {@link JwtTokenBuilder}
     */
    public JwtTokenBuilder generateJwtId(boolean jwtId) {
        optionsAdapter.setJwtId(jwtId);
        return this;
    }

    /**
     * Add JWT claim <tt>nbf</tt> to current timestamp - notValidBeforeLeeway
     *
     * @param notValidBeforeLeeway in seconds
     *
     * @return {@link JwtTokenBuilder}
     */
    public JwtTokenBuilder notValidBeforeLeeway(int notValidBeforeLeeway) {
        optionsAdapter.setNotValidBeforeLeeway(notValidBeforeLeeway);
        return this;
    }

    /**
     * Create a new JWT token
     *
     * @return JWT token
     *
     * @throws IllegalStateException if <tt>userId</tt> and <tt>roles</tt> (or their custom variants) are not provided
     */
    public String build() {
        Preconditions.checkState(claims.containsKey(customUserIdClaim) && claims.containsKey(customRolesClaim), customUserIdClaim + " and " + customRolesClaim + " claims must be added!");
        return jwtBuilder.sign(algorithm);
    }
}