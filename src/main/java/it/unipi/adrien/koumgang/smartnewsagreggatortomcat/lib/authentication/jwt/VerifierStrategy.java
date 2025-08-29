package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.authentication.jwt;


import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

interface VerifierStrategy {

    Algorithm verify(DecodedJWT token) throws RuntimeException;
}