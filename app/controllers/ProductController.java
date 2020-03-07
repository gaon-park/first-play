package controllers;

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
import java.util.Map;

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

    public Result addProduct(final Http.Request request) {

        Http.MultipartFormData body = request.body().asMultipartFormData();
        Http.MultipartFormData.FilePart<Files.TemporaryFile> image = body.getFile("image");
        Files.TemporaryFile temporaryFile = image.getRef();
        File file = temporaryFile.path().toFile();

        Map<String, String[]> map = body.asFormUrlEncoded();
        Product product = new Product();
        product.setName(map.get("name")[0]);
        product.setPrice(map.get("price")[0]);
        product.setInfo(map.get("info")[0]);

//        product.setFilePath(awsService.uploadFile(file));
//        productRepository.add(product);


//        productRepository.add(product).thenApplyAsync(p -> redirect(routes.ProductController.index()), context.current());

        return ok(product.toString());
    }

    public void getProducts(){
        System.out.println(productRepository.list());
    }
}
