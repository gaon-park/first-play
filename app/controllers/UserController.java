package controllers;

import models.response.UserResponse;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import service.JWTService;
import service.RedisService;
import service.UserService;

import javax.inject.Inject;

import java.util.Map;
import java.util.Optional;

import static play.libs.Json.toJson;

/**
 * this controller is necessary to get user information
 *
 * @author Gaon Park
 */
public class UserController extends Controller {
    private UserService userService;
    private JWTService jwtService;
    private RedisService redisService;

    @Inject
    public UserController(UserService userService, JWTService jwtService, RedisService redisService){
        this.userService = userService;
        this.jwtService = jwtService;
        this.redisService = redisService;
    }

    /**
     * you can access by 'get /user/:id' to get a one's information by using id from User table
     * @param id
     * @param request
     * @return Result
     */
    public Result getUserInfo(Integer id, Http.Request request){
        if(permissionCheck(request)){
            UserResponse userResponse = userService.getUserById(id);
            return ok(toJson(userResponse));
        }
        return Results.unauthorized();

//        if(request.session().get("id").isPresent()){
//            UserResponse userResponse = userService.getUserById(id);
//            return ok(toJson(userResponse));
//        }
//        return Results.unauthorized();
    }

    public boolean permissionCheck(Http.Request request){
        Optional<String> token  = request.getHeaders().get("accessToken");
        boolean result = false;
        if(token.isPresent()){
            if(!redisService.findBlacklist(request.session().hashCode(), token.get())){
                result = jwtService.validationToken(token.get());
            }
        }
        return result;
    }

    public Result createAccessToken(Http.Request request){
        Optional<String> accessToken = request.getHeaders().get("accessToken");
        Optional<String> refreshToken = request.getHeaders().get("refreshToken");
        Map<String, String> token = null;
        if(accessToken.isPresent() && refreshToken.isPresent()){
            token = jwtService.createAccessToken(accessToken.get(), refreshToken.get());
        }
        return ok(toJson(token));
    }
}
