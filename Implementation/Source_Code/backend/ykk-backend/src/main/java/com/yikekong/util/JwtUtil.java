package com.yikekong.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;

@Slf4j
public class JwtUtil {
    //Set secret key plaintext
    public static final String JWT_KEY = "yykkey";

    /**
     * Generate jwt token
     * @return
     */
    public static String createJWT(Integer adminId) {

        //Specify the encryption algorithm hs256 used in the signature
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        //Get the current time in milliseconds
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        //If the passed timeout parameter is empty, the default timeout of 1 hour is used

        ZoneId zoneId = ZoneId.systemDefault();
        //Use current time + timeout time = specific timeout at which point in time
        long expMillis = LocalDateTime.now().plusDays(7).atZone(zoneId).toInstant().toEpochMilli();
        //timeout
        Date expDate = new Date(expMillis);
        //Convert the incoming id and userName to json
        HashMap<String, Object> map = new HashMap<>();
        map.put("adminId",adminId);
        String s = null;
        try {
            s = JsonUtil.serialize(map);
        } catch (JsonProcessingException e) {
            log.error("json serialization failure",e);
        }

        //What is the key to specify encryption
        SecretKey secretKey = generalKey();

        JwtBuilder builder = Jwts.builder()
                .setSubject(s)
                .setIssuedAt(now)
                .signWith(signatureAlgorithm,secretKey)//Use hs256 cryptographic symmetric algorithm signature, the second is the secret key
                .setExpiration(expDate);//Set expiration time
        return builder.compact();

    }

    /**
     * Generate the encrypted secret key secretKey
     * @return
     */
    public static SecretKey generalKey() {
        byte[] encodedKey = Base64.getDecoder().decode(JwtUtil.JWT_KEY);
        SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        return key;
    }

    /**
     * Parse
     *
     * @param jwt
     * @return
     * @throws Exception
     */
    public static Claims parseJWT(String jwt) throws Exception {
        SecretKey secretKey = generalKey();
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(jwt)
                .getBody();
    }


}
