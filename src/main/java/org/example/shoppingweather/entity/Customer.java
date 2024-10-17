package org.example.shoppingweather.entity;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Entity
@Table(name = "customer")
public class Customer{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "loginId", nullable = false, unique = true)
    private String loginId;

    @Column(name = "password", nullable = false)
    private String password;

    private String name;
    private String email;
    private String phone;
    private String address;

    private String role;

    public Boolean isAdmin(){
        return role.equals("ROLE_ADMIN");
    }

    // UserDetails 메소드들...

    @Builder
    public Customer(String loginId, String password, String name, String email, String phone, String address,String role) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.role = role;
    }

}

