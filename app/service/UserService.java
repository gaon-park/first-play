package service;

import com.fasterxml.jackson.databind.JsonNode;
import models.database.User;
import models.response.UserResponse;
import repository.UserRepository;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

/**
 * Obtains the information you want in relation to the User table
 */
public class UserService {
    private UserRepository userRepository;

    @Inject
    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    /**
     * Check if the database contains user information in jsonNode and insert if not.
     * Then return the id of the user.
     *
     * @param jsonNode
     * @return id: int
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public int add(JsonNode jsonNode) throws ExecutionException, InterruptedException {
        int id = jsonNode.get("id").intValue();
        // Verify that there are users in the database
        CompletionStage<User> userCompletionStage = userRepository.select(id);
        if(userCompletionStage.toCompletableFuture().get() == null) {
            User user = new User();
            user.setId(id);
            String[] name = jsonNode.get("name").textValue().split(" ");
            user.setFirstName(name[name.length - 1]);
            String lastName = "";
            for(int i = 0; i < name.length - 1; i++)
                lastName += name[i];
            user.setLastName(lastName);
            user.setEmail(jsonNode.get("email").toString());
            userRepository.add(user);
        }
        return id;
    }

    /**
     * Use the primary key ID in the User table to obtain information from one user and map it to return it to the answer object.
     *
     * @param id
     * @return UserResponse
     */
    public UserResponse getUserById(int id){
        CompletionStage<User> userCompletionStage = userRepository.select(id);
        UserResponse userResponse = new UserResponse();
        try {
            User user = userCompletionStage.toCompletableFuture().get();
            userResponse.setEmail(user.getEmail());
            userResponse.setFirstName(user.getFirstName());
            userResponse.setLastName(user.getLastName());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return userResponse;
    }

    public void insertRefreshToken(int id, String refreshToken){
        UserResponse userResponse = getUserById(id);
        User user = new User();
        user.setId(id);
        user.setFirstName(userResponse.getFirstName());
        user.setLastName(userResponse.getLastName());
        user.setEmail(userResponse.getEmail());
        user.setRefreshToken(refreshToken);

        userRepository.update(user);
    }

    public String getRefreshToken(int id){
        CompletionStage<User> userCompletionStage = userRepository.select(id);
        String refreshToken = null;
        try{
            User user = userCompletionStage.toCompletableFuture().get();
            refreshToken = user.getRefreshToken();
        } catch (Exception e){
            e.printStackTrace();
        }
        return refreshToken;
    }
}
