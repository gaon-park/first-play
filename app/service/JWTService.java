package service;

import com.google.inject.ImplementedBy;

import java.util.Map;

@ImplementedBy(JWTServiceImpl.class)
public interface JWTService {
    Map<String, String> createToken(int id);
    Map<String, String> createAccessToken(String accessToken, String refreshToken);
    boolean validationToken(String jwt);
    Integer getIdInJWTToken(String jwt);
}
