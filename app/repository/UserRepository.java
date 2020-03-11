package repository;

import com.google.inject.ImplementedBy;
import models.User;

import java.util.concurrent.CompletionStage;

@ImplementedBy(JPAUserRepository.class)
public interface UserRepository {
    CompletionStage<User> add(User user);
    CompletionStage<User> select(int id);
}