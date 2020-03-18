package service;

import io.jsonwebtoken.*;
import javax.inject.Inject;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Use JWT for certification
 */
public class JWTService {
    private String KEY = "PLAY-PRODUCT";
    @Inject
    private RedisService redisService;

    /**
     * Use user id to generate 'access token' and 'refresh token'
     * and return them in the form of Map<String, String>
     * @param id
     * @return Map<String, String>
     */
    public Map<String, String> createToken(int id){
        Map<String, Object> header = new HashMap<>();
        header.put("typ", "JWT");
        header.put("alg", "HS256");

        Map<String, Object> payload = new HashMap<>();
        payload.put("id", id);
        String refreshToken = createToken(payload, 1*(1000 * 60 * 60 * 24 * 14)); // 2 weeks
        String accessToken = createToken(payload, 1 * (1000 * 60 * 20)); // 20 minutes

        Map<String, String> tokens = new HashMap<>();
        tokens.put("refreshToken", refreshToken);
        tokens.put("accessToken", accessToken);

        return tokens;
    }

    private String createToken(Map<String, Object> payload, long exp){
        Map<String, Object> header = new HashMap<>();
        header.put("typ", "JWT");
        header.put("alg", "HS256");

        String token = Jwts.builder()
                .setHeader(header)
                .setExpiration(new Date(System.currentTimeMillis() + exp))
                .setClaims(payload)
                .signWith(SignatureAlgorithm.HS256, KEY.getBytes())
                .compact();
        return token;
    }

    /**
     * The access token is a non-modulated token, and if the refresh token is not expired,
     * create a new access token and return it to form of Map
     * @param accessToken
     * @param refreshToken
     * @return Map<String, String>
     */
    public Map<String, String> createAccessToken(String accessToken, String refreshToken){
        String token = null;
        if(validationToken(accessToken) && getIdInJWTToken(refreshToken) != null){
            int id = getIdInJWTToken(refreshToken);
            if(redisService.getRefreshTokenById(id).equals(refreshToken)){
                Map<String, Object> payload = new HashMap<>();
                payload.put("id", id);
                token = createToken(payload, 1 * (1000 * 60 * 20)); // 20 minutes
            }
        }
        Map<String, String> map = new HashMap<>();
        map.put("accessToken", token);
        return map;
    }

    /**
     * Check the validity of the token
     * @param jwt
     * @return boolean
     */
    public boolean validationToken(String jwt) {
        try{
            Claims claims = Jwts.parser()
                    .setSigningKey(KEY.getBytes())
                    .parseClaimsJws(jwt)
                    .getBody();
        } catch(ExpiredJwtException e){
            return false;
        } catch(JwtException ex){
            return false;
        }
        return true;
    }

    /**
     * returns the id value from the data stored in the token
     * @param jwt
     * @return
     */
    public Integer getIdInJWTToken(String jwt){
        try{
            Claims claims = Jwts.parser()
                    .setSigningKey(KEY.getBytes())
                    .parseClaimsJws(jwt)
                    .getBody();
            return Integer.parseInt(String.valueOf(claims.get("id")));
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
