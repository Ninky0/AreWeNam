package org.example.shoppingweather.repository;

import org.example.shoppingweather.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Customer findByLoginId(String username);
}
