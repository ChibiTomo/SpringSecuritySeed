package net.chibidevteam.restappliseed.main.service;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class TokenManagerService {

    private static final Log LOGGER = LogFactory.getLog(TokenManagerService.class);

    private byte[]           secretKey;
    private JwtParser        tokenParser;

    @PostConstruct
    public void init() {
        String secret = "Top Secret";
        try {
            secretKey = secret.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.debug("UTF-8 is not supported. Using default secret", e);
            secretKey = secret.getBytes();
        }
        tokenParser = Jwts.parser().setSigningKey(secretKey);
    }

    public final String getUsernameFromToken(String token) {
        final Claims claims = getClaimsFromToken(token);
        if (claims == null) {
            return null;
        }
        return claims.getSubject();
    }

    private Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = tokenParser.parseClaimsJws(token).getBody();
        } catch (Exception e) {
            LOGGER.trace("Cannot retrieve claim from token", e);
            claims = null;
        }
        return claims;
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        UserDetails user = (UserDetails) userDetails;
        final String username = this.getUsernameFromToken(token);
        // final Date created = this.getCreatedDateFromToken(token);
        // final Date expiration = this.getExpirationDateFromToken(token);
        return username.equals(user.getUsername());
        // && !(this.isTokenExpired(token))
        // && !(this.isCreatedBeforeLastPasswordReset(created, user.getLastPasswordReset()));

    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", userDetails.getUsername());
        // claims.put("created", this.generateCurrentDate());
        return this.generateToken(claims);
    }

    private String generateToken(Map<String, Object> claims) {
        // try {
        return Jwts.builder() //
                .setClaims(claims) //
                // .setExpiration(this.generateExpirationDate())
                .signWith(SignatureAlgorithm.HS512, secretKey) //
                .compact();
        // } catch (UnsupportedEncodingException ex) {
        // //didn't want to have this method throw the exception, would rather log it and sign the token like it was before
        // logger.warn(ex.getMessage());
        // return Jwts.builder()
        // .setClaims(claims)
        // .setExpiration(this.generateExpirationDate())
        // .signWith(SignatureAlgorithm.HS512, this.secret)
        // .compact();
        // }
    }

}
