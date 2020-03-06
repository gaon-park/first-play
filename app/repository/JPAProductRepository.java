package repository;

import repository.DatabaseExecutionContext;
import models.Product;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class JPAProductRepository implements ProductRepository {

    private final JPAApi jpaApi;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public JPAProductRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext){
        this.jpaApi = jpaApi;
        this.executionContext = executionContext;
    }

    @Override
    public CompletionStage<Product> add(Product product) {
        return supplyAsync(
                () -> wrap(em -> insert(em, product)), executionContext);
    }

    @Override
    public CompletionStage<Stream<Product>> list() {
        return null;
    }

    private <T> T wrap(Function<EntityManager, T> function){
        return jpaApi.withTransaction(function);
    }

    private Product insert(EntityManager em, Product product){
        em.persist(product);
        return product;
    }

    private Stream<Product> list(EntityManager em){
        List<Product> productList = em.createQuery("select p from Product p", Product.class).getResultList();
        return productList.stream();
    }
}
