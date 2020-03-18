package service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * Class using redis for authentication system using jwt.
 */
public class RedisServiceImpl implements RedisService{
    private String host = "192.168.99.100";
    private int port = 6379;
    private int timeout = 0;
    private int db = 0;
    private JedisPoolConfig jedisPoolConfig;
    private JedisPool pool;
    private Jedis jedis;

    @Inject
    public RedisServiceImpl(){
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        pool = new JedisPool(jedisPoolConfig, host, port, timeout, null, db);
    }

    /**
     * put refresh token in redis
     * @param id
     * @param token
     */
    @Override
    public void setRefreshToken(int id, String token){
        jedis = pool.getResource();
        Map<String, String> map = new HashMap<>();
        map.put("refreshToken", token);
        jedis.hset(Integer.toString(id), map);
        jedis.expire(Integer.toString(id), 60 * 60 * 14 * 24); // 2 weeks
        if(jedis != null)
            jedis.close();
    }

    /**
     * return refresh token by id
     * @param id
     * @return token
     */
    @Override
    public String getRefreshTokenById(int id){
        jedis = pool.getResource();
        String token = jedis.hget(Integer.toString(id), "refreshToken");
        if(jedis != null)
            jedis.close();
        return token;
    }

    /**
     * delete refresh token from redis
     * @param id
     */
    @Override
    public void deleteRefreshToken(int id){
        jedis = pool.getResource();
        jedis.hdel(Integer.toString(id), "refreshToken");
        if(jedis != null)
            jedis.close();
    }

    /**
     * put access token in redis for blacklist
     * @param id
     * @param token
     */
    @Override
    public void setBlacklist(int id, String token){
        jedis = pool.getResource();
        jedis.hset(Integer.toString(id), "accessToken", token);
        if(jedis != null)
            jedis.close();
    }

    /**
     * if token exists in redis, return true
     * @param id
     * @param token
     * @return
     */
    @Override
    public boolean findBlacklist(int id, String token){
        jedis = pool.getResource();
        String accessToken = jedis.hget(Integer.toString(id), "accessToken");
        jedis.expire(Integer.toString(id), 60 * 20); // 20 minutes
        if(token.equals(accessToken))
            return true;
        return false;
    }
}
