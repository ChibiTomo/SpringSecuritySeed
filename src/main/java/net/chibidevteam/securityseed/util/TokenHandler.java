package net.chibidevteam.securityseed.util;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.chibidevteam.securityseed.security.authentication.ExpirableUserDetails;

public class TokenHandler {

    private static final Log    LOGGER          = LogFactory.getLog(TokenHandler.class);

    private static final String SEPARATOR       = ".";
    private static final String SPLIT_SEPARATOR = "\\" + SEPARATOR;
    private Mac                 mac;

    public TokenHandler(byte[] secretKey, String algo) {
        try {
            mac = Mac.getInstance(algo);
            mac.init(new SecretKeySpec(secretKey, algo));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalStateException("Failed to initialize algorythm (" + algo + "): " + e.getMessage(), e);
        }

    }

    public String createTokenForUser(UserDetails userDetails) {
        byte[] userBytes = toJSON(userDetails);
        byte[] hash = createHmac(userBytes);
        final StringBuilder sb = new StringBuilder(170);
        sb.append(toBase64(userBytes));
        sb.append(SEPARATOR);
        sb.append(toBase64(hash));
        return sb.toString();
    }

    public UserDetails parseUserFromToken(String token) {
        final String[] parts = token.split(SPLIT_SEPARATOR);
        if (parts.length != 2 || StringUtils.isEmpty(parts[0]) || StringUtils.isEmpty(parts[1])) {
            return null;
        }
        try {
            final byte[] userBytes = fromBase64(parts[0]);
            final byte[] hash = fromBase64(parts[1]);

            boolean validHash = Arrays.equals(createHmac(userBytes), hash);
            if (validHash) {
                final UserDetails user = fromJSON(userBytes);
                if (user != null) {// && new Date().getTime() < user.getExpires()) {
                    return user;
                }
            }
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Cannot retrieve user from token '" + token + "'", e);
        }
        return null;
    }

    private String toBase64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    private byte[] fromBase64(String str) {
        return Base64.getDecoder().decode(str);
    }

    private byte[] toJSON(UserDetails userDetails) {
        JSONObject obj = new JSONObject(new ExpirableUserDetails(userDetails));
        return obj.toString().getBytes();
    }

    private UserDetails fromJSON(byte[] bytes) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(bytes, ExpirableUserDetails.class);
        } catch (IOException e) {
            LOGGER.warn("Cannot read from JSON '" + new String(bytes) + "'", e);
            return null;
        }
    }

    private synchronized byte[] createHmac(byte[] content) {
        return mac.doFinal(content);
    }

}
