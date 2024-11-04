package org.example.shoppingweather.repository;

import org.example.shoppingweather.entity.Ootd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OotdRepository extends JpaRepository<Ootd, Long> {
    List<Ootd> findByCustomerId(Long customerId);
}
