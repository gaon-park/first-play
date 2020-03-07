package repository;

import com.google.inject.ImplementedBy;
import models.Product;

import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

@ImplementedBy(JPAProductRepository.class)
public interface ProductRepository {
    CompletionStage<Product> add(Product product);
    CompletionStage<Stream<Product>> list();
    CompletionStage<Product> select(int id);
    void remove(int id);
    CompletionStage<Product> update(Product product);
}
