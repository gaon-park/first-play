package services.interfaces;

import com.google.inject.ImplementedBy;
import models.request.ProductRequest;
import models.response.ProductResponse;
import services.ProductServiceImpl;

import java.util.List;

@ImplementedBy(ProductServiceImpl.class)
public interface ProductService {
    void addProduct(ProductRequest productRequest, String filePath);
    List<ProductResponse> getProducts();
    ProductResponse getProductById(int id);
    void removeProduct(int id);
    void updateProduct(ProductRequest productRequest, String filePath);
}
