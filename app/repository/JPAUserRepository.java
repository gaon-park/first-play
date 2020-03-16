package repository;

import models.database.User;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class JPAUserRepository implements UserRepository {
    private final JPAApi jpaApi;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public JPAUserRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        this.jpaApi = jpaApi;
        this.executionContext = executionContext;
    }

    @Override
    public CompletionStage<User> add(User user) {
        return supplyAsync(
                () -> wrap(em -> insert(em, user)), executionContext
        );
    }

    @Override
    public CompletionStage<User> select(int id) {
        return supplyAsync(() -> wrap(em -> select(em, id)), executionContext);
    }

    @Override
    public CompletionStage<User> update(User user){
        return supplyAsync(() -> wrap(em -> update(em, user)), executionContext);
    }

    private <T> T wrap(Function<EntityManager, T> function){
        return jpaApi.withTransaction(function);
    }

    private User insert(EntityManager em, User user){
        em.persist(user);
        return user;
    }

    private User select(EntityManager em, int id){
        return em.find(User.class, id);
    }

    private User update(EntityManager em, User user){
        return em.merge(user);
    }
}
