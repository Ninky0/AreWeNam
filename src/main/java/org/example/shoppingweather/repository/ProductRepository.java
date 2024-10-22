package org.example.shoppingweather.repository;

import org.example.shoppingweather.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findById(Long id);

    @Query("SELECT MAX(p.id) FROM Product p")
    Long findMaxId();
}
