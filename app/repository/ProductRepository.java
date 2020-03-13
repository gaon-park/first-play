package repository;

import com.google.inject.ImplementedBy;
import models.database.Product;

import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

@ImplementedBy(JPAProductRepository.class)
public interface ProductRepository {
    CompletionStage<Product> add(Product product);
    CompletionStage<Stream<Product>> getList();
    CompletionStage<Product> select(int id);
    void remove(int id);
    CompletionStage<Product> update(Product product);
}
