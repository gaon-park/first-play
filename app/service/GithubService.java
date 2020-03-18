package service;

import com.fasterxml.jackson.databind.JsonNode;
import play.libs.ws.WSClient;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

/**
 * This class has the ability to use github oauth api.
 */
public class GithubService {
    private final static String CLIENT_ID = "2f0f7ccd9659b70d0bc5";
    private final static String CLIENT_SECRET = "6f0d56d94315681b0eed8789f4260e97ad40988f";
    private final static String REDIRECT_URI = "http://localhost:9000/callback";
    private String ACCESS_TOKEN;

    private final WSClient ws;

    @Inject
    public GithubService(WSClient ws){
        this.ws = ws;
    }

    /**
     * Generate oauth url based on CLIENT_ID and REDIRECT information for this application.
     * @return url
     */
    public String getGitOAuthUrl(){
        //get code from github
        String url = "https://github.com/login/oauth/authorize?client_id=" + CLIENT_ID + "&redirect_uri=" + REDIRECT_URI;
        return url;
    }

    /**
     * Request a user access token using code received through url as parameter
     * @param code
     * @return ACCESS_TOKEN
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public String getAccessToken(String code) throws ExecutionException, InterruptedException {
        // get access_token from github
        // not main
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
        return ACCESS_TOKEN;
    }

    /**
     * request user api from git hub
     * @return JsonNode
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public JsonNode getUserInfo() throws ExecutionException, InterruptedException {
        // get user information from github
        CompletionStage<JsonNode> completionStage = ws.url("https://api.github.com/user")
                .addHeader("Authorization", "Token " + ACCESS_TOKEN)
                .get().thenApply(r -> r.asJson());
        JsonNode jsonNode = completionStage.toCompletableFuture().get();
        return jsonNode;
    }
}
