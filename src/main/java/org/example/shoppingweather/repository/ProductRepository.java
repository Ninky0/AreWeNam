package org.example.shoppingweather.repository;

import org.example.shoppingweather.entity.Customer;
import org.example.shoppingweather.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByProductId(String productId);

}
