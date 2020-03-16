package controllers;

import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import service.GithubService;
import service.JWTService;
import service.UserService;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static play.libs.Json.toJson;

/**
 * this controller is necessary by using to login with github oauth api
 *
 * @author Gaon Park
 */
public class LoginController extends Controller {
    private GithubService githubService;
    private UserService userService;
    private JWTService jwtService;

    @Inject
    public LoginController(GithubService githubService, UserService userService, JWTService jwtService){
        this.githubService = githubService;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    /**
     * you can access by 'get /login' to login
     * @return Result
     */
    public Result sendGitOAuth(){
        return redirect(githubService.getGitOAuthUrl());
    }

    /**
     * This is a function that is called back automatically by github.
     * if you got access_token from git hub, you can get jwt token.
     * @param code
     * @param request
     * @return
     */
    public Result callback(String code, Http.Request request) {
        // main thread
        // not main

        int id = 0;
        try {
            githubService.getAccessToken(code);
            id = userService.add(githubService.getUserInfo());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Map<String, String> tokens = jwtService.createToken(id);
        userService.insertRefreshToken(id, tokens.get("refreshToken"));
        return ok(toJson(tokens));
    }

    /**
     * you can logout by 'get /logout'.
     * you lose your permission.
     * @param request
     * @return Result
     */
    public Result logout(Http.Request request){
        return ok("logout").removingFromSession(request, "id").removingFromSession(request, "access_token");
    }
}
