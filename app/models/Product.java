package models;

import javax.persistence.*;
@Entity
@Table(name = "product", schema = "playdb")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public int id;
    @Column(name = "name")
    public String name;
    @Column(name = "info")
    public String info;
    @Column(name = "price")
    public String price;
    @Column(name = "filepath")
    public String filePath;

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
