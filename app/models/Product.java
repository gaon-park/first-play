package models;

import javax.persistence.*;
@Entity
@Table(name = "product", schema = "playdb")
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
    @Column(name = "filepath")
    private String filePath;

    public Product(){
        name = "";
        info = "";
        price = "";
        filePath = "";
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", info='" + info + '\'' +
                ", price='" + price + '\'' +
                ", filePath='" + filePath + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getPrice() {
        return price;
    }

    public String getInfo() {
        return info;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getFilePath() {
        return filePath;
    }
}
