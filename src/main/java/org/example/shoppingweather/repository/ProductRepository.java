package org.example.shoppingweather.repository;

import org.apache.ibatis.annotations.Param;
import org.example.shoppingweather.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findById(Long id);
    Page<Product> findAll(Pageable pageable);
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
    List<Product> findByTemperature(int tempIndex);

    @Query("SELECT p FROM Product p WHERE p.season = :season")
    Page<Product> findBySeason(@Param("season") String season, Pageable pageable);

}
