package service;

import com.google.inject.ImplementedBy;

@ImplementedBy(RedisServiceImpl.class)
public interface RedisService {
    void setRefreshToken(int id, String token);
    String getRefreshTokenById(int id);
    void deleteRefreshToken(int id);
    void setBlacklist(int id, String token);
    boolean findBlacklist(int id, String token);
}
