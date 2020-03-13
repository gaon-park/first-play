package models.database;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "user", schema = "playdb")
@Data
public class User {
    @Id
    @Column(name = "id")
    private int id;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private  String lastName;
    @Column(name = "email")
    private String email;
}
