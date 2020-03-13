package controllers;

import models.response.UserResponse;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import service.UserService;

import javax.inject.Inject;

import static play.libs.Json.toJson;

/**
 * this controller is necessary to get user information
 *
 * @author Gaon Park
 */
public class UserController extends Controller {
    private UserService userService;

    @Inject
    public UserController(UserService userService){
        this.userService = userService;
    }

    /**
     * you can access by 'get /user/:id' to get a one's information by using id from User table
     * @param id
     * @param request
     * @return Result
     */
    public Result getUserInfo(Integer id, Http.Request request){
        if(request.session().get("id").isPresent()){
            UserResponse userResponse = userService.getUserById(id);
            return ok(toJson(userResponse));
        }
        return Results.unauthorized();
    }
}
