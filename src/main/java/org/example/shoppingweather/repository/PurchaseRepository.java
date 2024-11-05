package org.example.shoppingweather.repository;

import org.example.shoppingweather.entity.Customer;
import org.example.shoppingweather.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    List<Purchase> findByCustomer(Customer customer);
    List<Purchase> findByCustomerId(Long customerId);
}
