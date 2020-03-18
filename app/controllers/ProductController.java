package controllers;

import models.request.ProductRequest;
import models.response.ProductResponse;
import play.data.Form;
import play.mvc.Http.MultipartFormData.FilePart;
import services.interfaces.AWSService;
import models.database.Product;
import play.data.FormFactory;
import play.libs.Files;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import services.interfaces.ProductService;

import javax.inject.Inject;
import java.util.List;
import static play.libs.Json.toJson;

/**
 * this controller is necessary to get product information
 *
 * @author Gaon Park
 */
public class ProductController extends Controller {
    private FormFactory formFactory;
    private AWSService awsService;
    private ProductService productService;

    @Inject
    public ProductController(FormFactory formFactory, ProductService productService, AWSService awsService){
        this.formFactory = formFactory;
        this.awsService = awsService;
        this.productService = productService;
    }
    /**
     * you can access by 'post /product' to add Product
     * (required form field)
     * Return the Product object just inserted
     * @param request
     * @return Result
     */
    public Result addProduct(Http.Request request) {

        Form<ProductRequest> productForm = formFactory.form(ProductRequest.class).bindFromRequest(request);
        if(productForm.hasErrors()){
            return badRequest();
        } else {
            ProductRequest productRequest = productForm.get();
            Http.MultipartFormData body = request.body().asMultipartFormData();
            FilePart<Files.TemporaryFile> image = body.getFile("image");
            String filePath = awsService.uploadFile(image.getRef().path().toFile());
            productService.addProduct(productRequest, filePath);
            return redirect("/products");
        }
    }

    /**
     * you can access by 'get /products' to get list of product in Product table
     * @return Result
     */
    public Result getProducts(){
//        System.out.println(productService.getProducts());
        List<ProductResponse> list = productService.getProducts();
        return ok(toJson(list));
    }

    /**
     * you can access by 'get /product/:id' to get one product's information by id
     * @param id
     * @return Result
     */
    public Result getProductById(Integer id){
        ProductResponse productResponse = productService.getProductById(id);
        return ok(toJson(productResponse));
    }

    /**
     * you can access by 'delete /product/:id' to delete one product by id from Product table
     * @param id
     * @return Result
     */
    public Result removeProduct(Integer id){
        productService.removeProduct(id);
        return redirect(routes.ProductController.getProducts());
    }

    /**
     * you can access by 'put /product' to update
     * (required form field)
     * Return the Product object just updated
     * @param request
     * @return Result
     */
    public Result updateProduct(Http.Request request){

        Form<ProductRequest> productForm = formFactory.form(ProductRequest.class).bindFromRequest(request);
        if(productForm.hasErrors()){
            return badRequest();
        } else {
            ProductRequest productRequest = productForm.get();

            // remove current file
            awsService.removeFile(productService.getProductById(productRequest.getId()).getFilePath());

            Http.MultipartFormData body = request.body().asMultipartFormData();
            FilePart<Files.TemporaryFile> image = body.getFile("image");
            Product product = new Product();

            // upload new file
            String filePath = awsService.uploadFile(image.getRef().path().toFile());
            productService.updateProduct(productRequest, filePath);
            return redirect("/products");
        }
    }
}
