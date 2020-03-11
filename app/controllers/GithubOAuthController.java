package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.User;
import play.libs.ws.WSClient;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import repository.UserRepository;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

public class GithubOAuthController extends Controller {
    private final static String CLIENT_ID = "2f0f7ccd9659b70d0bc5";
    private final static String CLIENT_SECRET = "6f0d56d94315681b0eed8789f4260e97ad40988f";
    private final static String REDIRECT_URI = "http://localhost:9000/callback";
    private String ACCESS_TOKEN;

    private final WSClient ws;
    private UserRepository userRepository;

    @Inject
    public GithubOAuthController(WSClient ws, UserRepository userRepository){
        this.ws = ws;
        this.userRepository = userRepository;
    }

    public Result index(){
        return ok(String.valueOf(notAcceptable()));
    }

    public Result sendGitOAuth(){
        String url = "https://github.com/login/oauth/authorize?client_id=" + CLIENT_ID + "&redirect_uri=" + REDIRECT_URI;
        return redirect(url);
    }

    public Result callback(String code, Http.Request request){
        CompletionStage<JsonNode> completionStage = ws.url("https://github.com/login/oauth/access_token")
                .addHeader("Accept", "application/json")
                .addQueryParameter("code", code)
                .addQueryParameter("client_id",CLIENT_ID)
                .addQueryParameter("client_secret", CLIENT_SECRET)
                .execute("POST")
                .thenApply(r -> r.asJson());
        JsonNode jsonNode = null;
        try {
            jsonNode = completionStage.toCompletableFuture().get();
            ACCESS_TOKEN = jsonNode.get("access_token").asText();
        } catch (Exception e) {
            e.printStackTrace();
        }

        setUser(request);

        return ok(jsonNode);
    }

    private void setUser(Http.Request request){
        CompletionStage<JsonNode> completionStage = ws.url("https://api.github.com/user")
                .addHeader("Authorization", "Token " + ACCESS_TOKEN)
                .get().thenApply(r -> r.asJson());
        JsonNode jsonNode = null;
        try {
            jsonNode = completionStage.toCompletableFuture().get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        int id = Integer.parseInt(jsonNode.get("id").toString());
        CompletionStage<User> userCompletionStage = userRepository.select(id);
        if(userCompletionStage == null) {
            User user = new User();
            user.setId(id);
            user.setName(jsonNode.get("name").toString());
            user.setHtml_url(jsonNode.get("html_url").toString());

            userRepository.add(user);

            request.session().adding("id", Integer.toString(id));
        }
    }
}
