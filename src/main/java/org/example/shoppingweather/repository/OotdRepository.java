package org.example.shoppingweather.repository;

import org.example.shoppingweather.entity.Ootd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OotdRepository extends JpaRepository<Ootd, Long> {
    // 기본적인 CRUD 기능이 자동 제공됩니다.
}
