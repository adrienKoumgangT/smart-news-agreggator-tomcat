package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.authentication.jwt;


import com.auth0.jwt.JWTCreator;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * Internal Option class adapter to maintain signatures with previous version of java-jwt library
 *
 * @author acomo
 */
class OptionsAdapter {

    private final JWTCreator.Builder jwtBuilder;

    private final Date currentDate;

    OptionsAdapter(JWTCreator.Builder builder) {
        this.jwtBuilder = builder;
        this.currentDate = new Date();
    }

    void setJwtId(Boolean jwtId) {
        if (jwtId) {
            jwtBuilder.withJWTId(UUID.randomUUID().toString());
        }
    }

    void setIssuedAt(Boolean issuedAt) {
        if (issuedAt) {
            jwtBuilder.withIssuedAt(currentDate);
        }
    }

    void setExpirySeconds(Integer expirySeconds) {
        if (expirySeconds != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentDate);
            calendar.add(Calendar.SECOND, expirySeconds);
            jwtBuilder.withExpiresAt(calendar.getTime());
        }
    }

    void setNotValidBeforeLeeway(Integer notValidBeforeLeeway) {
        if (notValidBeforeLeeway != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentDate);
            calendar.add(Calendar.SECOND, -1 * notValidBeforeLeeway);
            jwtBuilder.withNotBefore(calendar.getTime());
        }
    }
}
