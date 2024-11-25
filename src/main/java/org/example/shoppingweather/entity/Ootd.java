package org.example.shoppingweather.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.shoppingweather.dto.ootd.OotdWriteRequestDTO;

import java.time.LocalDateTime;

@Table(name = "ootd") // Sets table name as "ootd"
@NoArgsConstructor(access = AccessLevel.PUBLIC) // Allows public access to the default constructor
@Getter
@Setter
@Entity
public class Ootd {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generated ID
    private Long id;  // Unique identifier for OOTD

    @ManyToOne
    @JoinColumn(name = "productId", nullable = false)  // Links to the associated product ID
    private Product product; // Single product field

    @ManyToOne
    @JoinColumn(name = "customerId", nullable = false)  // Links to the associated customer ID
    private Customer customer;

    @Column(nullable = false)
    private LocalDateTime date; // Date and time of creation

    @Column(nullable = false)
    private String picture; // Stores the image path

    private String tag; // Tag information

    // Static factory method to create Ootd from DTO
    public static Ootd fromDTO(OotdWriteRequestDTO requestDTO, String picturePath, Customer customer, Product product) {
        Ootd ootd = new Ootd();
        ootd.setProduct(product); // Set the linked Product
        ootd.setCustomer(customer);
        ootd.setDate(LocalDateTime.now());
        ootd.setPicture(picturePath);
        ootd.setTag(requestDTO.getTag());
        return ootd;
    }

    @PrePersist
    protected void onCreate() {
        this.date = LocalDateTime.now();  // Automatically sets the date when the entity is created
    }
}