package service;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.ImplementedBy;

import java.util.concurrent.ExecutionException;

@ImplementedBy(GithubServiceImpl.class)
public interface GithubService {
    String getGitOAuthUrl();
    String getAccessToken(String code) throws ExecutionException, InterruptedException;
    JsonNode getUserInfo() throws ExecutionException, InterruptedException;
}
