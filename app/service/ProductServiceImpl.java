package service;

import models.database.Product;
import models.request.ProductRequest;
import models.response.ProductResponse;
import repository.ProductRepository;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

/**
 * Obtains the information you want in relation to the Product table
 */
public class ProductServiceImpl implements ProductService {
    private ProductRepository productRepository;

    @Inject
    public ProductServiceImpl(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    /**
     * Obtain the ProductRequest object and String object, map it to the Product object,
     * and insert it into the table.
     *
     * @param productRequest
     * @param filePath
     */
    @Override
    public void addProduct(ProductRequest productRequest, String filePath){
        Product product = new Product();
        product.setName(productRequest.getName());
        product.setInfo(productRequest.getInfo());
        product.setPrice(productRequest.getPrice());
        product.setFilePath(filePath);
        productRepository.add(product);
    }

    /**
     * Return all product information that exists in the Product table
     * @return List<ProductResponse>
     */
    @Override
    public List<ProductResponse> getProducts(){
        CompletionStage<Stream<Product>> list = productRepository.getList();
        List<ProductResponse> responseList = new ArrayList<>();
        try {
            list.toCompletableFuture().get().forEach(product -> {
                ProductResponse productResponse = new ProductResponse();
                productResponse.setId(product.getId());
                productResponse.setName(product.getName());
                productResponse.setInfo(product.getInfo());
                productResponse.setPrice(product.getPrice());
                productResponse.setFilePath(product.getFilePath());
                responseList.add(productResponse);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return responseList;
    }

    /**
     * Use the primary key ID in the Product table to obtain information from one product and map it to return it to the answer object.
     *
     * @param id
     * @return ProductResponse
     */
    @Override
    public ProductResponse getProductById(int id){
        ProductResponse productResponse = new ProductResponse();
        try {
            Product product = productRepository.select(id).toCompletableFuture().get();
            productResponse.setId(product.getId());
            productResponse.setName(product.getName());
            productResponse.setInfo(product.getInfo());
            productResponse.setPrice(product.getPrice());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return productResponse;
    }

    /**
     * Use the id value to delete the information that owns the id from the Product table.
     *
     * @param id
     */
    @Override
    public void removeProduct(int id){
        productRepository.remove(id);
    }

    /**
     * Update an object with the value of that id present in the Product table using the requested object and the String value.
     * @param productRequest
     * @param filePath
     */
    @Override
    public void updateProduct(ProductRequest productRequest, String filePath){
        Product product = new Product();
        product.setName(productRequest.getName());
        product.setInfo(productRequest.getInfo());
        product.setPrice(productRequest.getPrice());
        product.setFilePath(filePath);
        productRepository.update(product);
    }
}
