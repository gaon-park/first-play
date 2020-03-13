package repository;

import models.database.Product;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
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
    public CompletionStage<Stream<Product>> getList() {
        return supplyAsync(()->wrap(em -> getList(em)), executionContext);
    }

    @Override
    public CompletionStage<Product> select(int id){
        return supplyAsync(()->wrap(em->select(em,id)),executionContext);
    }

    @Override
    public void remove(int id){
        jpaApi.withTransaction(em -> {
            Query query = em.createNativeQuery("delete from product where id=" + Integer.toString(id));
            query.executeUpdate();
        });
    }

    @Override
    public CompletionStage<Product> update(Product product){
        return supplyAsync(()->wrap(em->update(em, product)),executionContext);
    }

    private <T> T wrap(Function<EntityManager, T> function){
        return jpaApi.withTransaction(function);
    }

    private Product insert(EntityManager em, Product product){
        em.persist(product);
        return product;
    }

    private Stream<Product> getList(EntityManager em){
        List<Product> productList = em.createQuery("select p from Product p", Product.class).getResultList();
        return productList.stream();
    }

    private Product select(EntityManager em, int id){
        return em.find(Product.class, id);
    }

    private Product update(EntityManager em, Product product){
        return em.merge(product);
    }
}
