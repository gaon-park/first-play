package controllers;

import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import service.GithubService;
import service.UserService;

import javax.inject.Inject;
import java.util.concurrent.ExecutionException;

/**
 * this controller is necessary by using to login with github oauth api
 *
 * @author Gaon Park
 */
public class LoginController extends Controller {
    private GithubService githubService;
    private UserService userService;

    @Inject
    public LoginController(GithubService githubService, UserService userService){
        this.githubService = githubService;
        this.userService = userService;
    }

    /**
     * you can access by 'get /login' to login
     * @return
     */
    public Result sendGitOAuth(){
        return redirect(githubService.getGitOAuthUrl());
    }

    /**
     * This is a function that is called back automatically by github.
     * if you got access_token from git hub, you can get permission by session.
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

        return redirect("/user/" + id).addingToSession(request, "id", Integer.toString(id));
    }

    /**
     * you can logout by 'get /logout'.
     * you lose your permission.
     * @param request
     * @return
     */
    public Result logout(Http.Request request){
        return ok("logout").removingFromSession(request, "id").removingFromSession(request, "access_token");
    }
}
