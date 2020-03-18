package service;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.ImplementedBy;
import models.response.UserResponse;

import java.util.concurrent.ExecutionException;

@ImplementedBy(UserServiceImpl.class)
public interface UserService {
    int add(JsonNode jsonNode) throws ExecutionException, InterruptedException;
    UserResponse getUserById(int id);

}
