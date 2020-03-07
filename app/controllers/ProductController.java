package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import repository.JPAProductRepository;
import service.AWSService;
import models.Product;
import repository.ProductRepository;
import play.data.FormFactory;
import play.libs.Files;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static play.libs.Json.toJson;

public class ProductController extends Controller {
    private final FormFactory formFactory;
    private final ProductRepository productRepository;
    private final HttpExecutionContext context;
    private AWSService awsService;

    @Inject
    public ProductController(FormFactory formFactory, ProductRepository productRepository, HttpExecutionContext context){
        this.formFactory = formFactory;
        this.productRepository = productRepository;
        this.context = context;
        awsService = new AWSService();
    }

    public Result index(){
        return ok("hello!");
    }

    public CompletionStage<Result> addProduct(final Http.Request request) {

        Http.MultipartFormData body = request.body().asMultipartFormData();
        Http.MultipartFormData.FilePart<Files.TemporaryFile> image = body.getFile("image");
        Files.TemporaryFile temporaryFile = image.getRef();
        File file = temporaryFile.path().toFile();

        Map<String, String[]> map = body.asFormUrlEncoded();

        Product product = setProductValue(map, new Product());
        product.setFilePath(awsService.uploadFile(file));

        CompletionStage<Product> productCompletionStage = productRepository.add(product);
        return productCompletionStage.thenApplyAsync(p -> ok(toJson(product)), context.current());
    }

    public CompletionStage<Result> getProducts(){
        CompletionStage<Stream<Product>> list = productRepository.list();
        return list.thenApplyAsync(productStream -> ok(toJson(productStream.collect(Collectors.toList()))), context.current());
    }

    public CompletionStage<Result> getOneProduct(Integer id){
        CompletionStage<Product> productCompletionStage = productRepository.select(id);
        return productCompletionStage.thenApplyAsync(product -> ok(toJson(product)), context.current());
    }

    public Result removeProduct(Integer id){
        productRepository.remove(id);
        return redirect(routes.ProductController.getProducts());
    }

    public CompletionStage<Result> modifyProduct(final Http.Request request){
        Http.MultipartFormData body = request.body().asMultipartFormData();
        Http.MultipartFormData.FilePart<Files.TemporaryFile> image = body.getFile("image");

        Map<String, String[]> map = body.asFormUrlEncoded();

        CompletionStage<Product> productCompletionStage = productRepository.select(Integer.parseInt(map.get("id")[0]));
        Product product = null;
        try {
            product = productCompletionStage.toCompletableFuture().get();
            if(image != null) {
                // remove current file
                awsService.removeFile(product.getFilePath());
                // upload new file
                product.setFilePath(awsService.uploadFile(image.getRef().path().toFile()));
            }
            product = setProductValue(map, product);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        productCompletionStage = productRepository.update(product);
        Product finalProduct = product;
        return productCompletionStage.thenApplyAsync(p -> ok(toJson(finalProduct)), context.current());
    }

    private Product setProductValue(Map<String, String[]> map, Product product){
        Product result = new Product();

        //because of the auto_increment makes number
        if(Integer.valueOf(product.getId()) != null) {
            result.setId(product.getId());
        }
        result.setName((map.get("name")[0] != null) ? map.get("name")[0] : product.getName());
        result.setPrice((map.get("price")[0] != null) ? map.get("price")[0] : product.getPrice());
        result.setInfo((map.get("info")[0] != null) ? map.get("info")[0] : product.getInfo());
        result.setFilePath(product.getFilePath());

        return result;
    }
}
