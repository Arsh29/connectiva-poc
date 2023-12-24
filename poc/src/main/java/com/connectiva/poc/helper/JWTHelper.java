package com.connectiva.poc.helper;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecretKeyBuilder;
import io.jsonwebtoken.security.SecureDigestAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
@Component
public class JWTHelper {
    //requirement :
    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

    //    public static final long JWT_TOKEN_VALIDITY =  60;

    private String secret = "afafasfafafasfasfasfafacasdasfasxASFACASDFACASDFASFASFDAFASFASDAADSCSDFADCVSGCFVADXCcadwavfsfarvf";

    //retrieve username from jwt token
    public String getUsernameFromToken(String token) throws NoSuchAlgorithmException {
        return getClaimFromToken(token, Claims::getSubject);
    }

    //retrieve expiration date from jwt token
    public Date getExpirationDateFromToken(String token) throws NoSuchAlgorithmException {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) throws NoSuchAlgorithmException {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private SecretKey getSecretKey(String secret) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        byte[] keyBytes = digest.digest(secretBytes);
        return new SecretKeySpec(keyBytes, "HmacSHA256");
    }
    //for retrieveing any information from token we will need the secret key
    private Claims getAllClaimsFromToken(String token) throws NoSuchAlgorithmException {

        return Jwts.parser().verifyWith(getSecretKey(secret)).build().parseSignedClaims(token).getPayload();
    }

    //check if the token has expired
    private Boolean isTokenExpired(String token) throws NoSuchAlgorithmException {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    //generate token for user
    public String generateToken(UserDetails userDetails) throws NoSuchAlgorithmException {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, userDetails.getUsername());
    }

    //while creating the token -
    //1. Define  claims of the token, like Issuer, Expiration, Subject, and the ID
    //2. Sign the JWT using the HS512 algorithm and secret key.
    //3. According to JWS Compact Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
    //   compaction of the JWT to a URL-safe string
    private String doGenerateToken(Map<String, Object> claims, String subject) throws NoSuchAlgorithmException {
        return  Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(getSecretKey(secret))
                .compact();

    }

    //validate token
    public Boolean validateToken(String token, UserDetails userDetails) throws NoSuchAlgorithmException {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
