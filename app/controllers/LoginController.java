package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.User;
import play.libs.concurrent.HttpExecutionContext;
import play.libs.ws.WSClient;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import repository.UserRepository;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import static play.libs.Json.toJson;

public class LoginController extends Controller {
    private final static String CLIENT_ID = "2f0f7ccd9659b70d0bc5";
    private final static String CLIENT_SECRET = "6f0d56d94315681b0eed8789f4260e97ad40988f";
    private final static String REDIRECT_URI = "http://localhost:9000/callback";
    private String ACCESS_TOKEN;

    private final WSClient ws;
    private final UserRepository userRepository;
    private final HttpExecutionContext context;

    @Inject
    public LoginController(WSClient ws, UserRepository userRepository, HttpExecutionContext context){
        this.ws = ws;
        this.userRepository = userRepository;
        this.context = context;
    }

    public Result sendGitOAuth(){
        //get code from github
        String url = "https://github.com/login/oauth/authorize?client_id=" + CLIENT_ID + "&redirect_uri=" + REDIRECT_URI;
        return redirect(url);
    }

    public Result callback(String code, Http.Request request) throws ExecutionException, InterruptedException {
        // get access_token from github
        CompletionStage<JsonNode> completionStage = ws.url("https://github.com/login/oauth/access_token")
                .addHeader("Accept", "application/json")
                .addQueryParameter("code", code)
                .addQueryParameter("client_id",CLIENT_ID)
                .addQueryParameter("client_secret", CLIENT_SECRET)
                .execute("POST")
                .thenApply(r -> r.asJson());
        JsonNode jsonNode = null;

        jsonNode = completionStage.toCompletableFuture().get();
        ACCESS_TOKEN = jsonNode.get("access_token").asText();

        // get user information from github
        completionStage = ws.url("https://api.github.com/user")
                .addHeader("Authorization", "Token " + ACCESS_TOKEN)
                .get().thenApply(r -> r.asJson());

        jsonNode = completionStage.toCompletableFuture().get();

        int id = Integer.parseInt(jsonNode.get("id").toString());

        // Verify that there are users in the database
        CompletionStage<User> userCompletionStage = userRepository.select(id);
        if(userCompletionStage.toCompletableFuture().get() == null) {
            User user = new User();
            user.setId(id);
            user.setName(jsonNode.get("name").toString());
            user.setHtml_url(jsonNode.get("html_url").toString());

            userRepository.add(user);
        }

        return redirect("/user/" + id).addingToSession(request, "id", Integer.toString(id)).addingToSession(request, "access_token", ACCESS_TOKEN);
    }

    public CompletionStage<Result> getUserInfo(Integer id, Http.Request request){
        if(request.session().get("id").isPresent()){
            CompletionStage<User> completionStage = userRepository.select(id);
            return completionStage.thenApplyAsync(user -> ok(toJson(user)), context.current());
        }
        return CompletableFuture.completedFuture(Results.unauthorized());
    }

    public Result logout(Http.Request request){
        return ok("logout").removingFromSession(request, "id").removingFromSession(request, "access_token");
    }
}
