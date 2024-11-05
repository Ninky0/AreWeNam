package org.example.shoppingweather.repository;

import org.example.shoppingweather.entity.Ootd;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface OotdRepository extends JpaRepository<Ootd, Long> {
    Page<Ootd> findByCustomerId(Long customerId, Pageable pageable);
}
