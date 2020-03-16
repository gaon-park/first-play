package service;

import io.jsonwebtoken.*;

import javax.inject.Inject;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JWTService {
    private String KEY = "PLAY-PRODUCT";
    @Inject
    private UserService userService;

    public Map<String, String> createToken(int id){
        Map<String, Object> header = new HashMap<>();
        header.put("typ", "JWT");
        header.put("alg", "HS256");

        Map<String, Object> payload = new HashMap<>();
        payload.put("id", id);
        String refreshToken = createToken(payload, 1*(1000 * 60 * 60 * 24));
        String accessToken = createToken(payload, 1 * (1000 * 60));

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

    public Map<String, String> createAccessToken(String accessToken, String refreshToken){
        String token = null;
        if(validationToken(accessToken) && getIdInJWTToken(refreshToken) != null){
            int id = getIdInJWTToken(refreshToken);
            if(refreshToken.equals(userService.getRefreshToken(id))){
                Map<String, Object> payload = new HashMap<>();
                payload.put("id", id);
                token = createToken(payload, 1 * (1000 * 60));
            }
        }
        Map<String, String> map = new HashMap<>();
        map.put("accessToken", token);
        return map;
    }

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
