package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.controller;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.authentication.token.TokenManager;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.authentication.user.UserToken;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.exception.safe.InvalidAuthentificationException;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.exception.safe.InvalidTokenException;

public class BaseController {

    public static UserToken getUserToken(String token) throws Exception {
        if(token == null) throw new InvalidTokenException("token is null");

        try  {
            return TokenManager.readToken(token.replace("Bearer ", ""));
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidAuthentificationException("Invalid token");
        }
    }

}
