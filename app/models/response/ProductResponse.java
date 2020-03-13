package models.response;

import lombok.Data;

@Data
public class ProductResponse {
    private int id;
    private String name;
    private String info;
    private String price;
    private String filePath;
    // thread
}
