package models.request;

import lombok.Data;

@Data
public class ProductRequest {
    private int id;
    private String name;
    private String info;
    private String price;
}
