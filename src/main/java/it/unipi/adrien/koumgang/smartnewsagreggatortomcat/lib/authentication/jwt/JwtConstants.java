package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.authentication.jwt;


/**
 * Helper class for centralizing constants
 *
 * @author acomo
 *
 */
public class JwtConstants {

    /**
     * Authentication header
     */
    public static final String AUTH_HEADER = "X-Auth";

    /**
     * Authentication header
     */
    public static final String AUTH_PARAM = "access_token";

    /**
     * User Id claim key
     */
    public static final String USER_ID = "userId";

    /**
     * Roles claim key
     */
    public static final String ROLES = "roles";

}