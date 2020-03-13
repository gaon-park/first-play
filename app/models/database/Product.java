package models.database;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "product", schema = "playdb")
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "info")
    private String info;
    @Column(name = "price")
    private String price;
    @Column(name = "file_path")
    private String filePath;

    public Product(){
        name = "";
        info = "";
        price = "";
        filePath = "";
    }
}
