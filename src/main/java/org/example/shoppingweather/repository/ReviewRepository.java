package org.example.shoppingweather.repository;

import org.example.shoppingweather.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    // Product 엔티티와 연관된 리뷰를 찾는 메소드
    Page<Review> findByProductId(Long productId, Pageable pageable);

    // 상품 ID에 따른 리뷰 개수 조회
    @Query("SELECT COUNT(r) FROM Review r WHERE r.product.id = :productId")
    Long countByProductId(Long productId);

    Page<Review> findByCustomerId(Long customerId, Pageable pageable);
}
