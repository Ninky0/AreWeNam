package org.example.shoppingweather.repository;

import org.example.shoppingweather.entity.Ootd;
import org.example.shoppingweather.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository {

    // 공통 게시물 조회
    List<Ootd> findOotdByCustomerId(Long customerId);

    List<Review> findReviewByCustomerId(Long customerId);

    Page<Ootd> findOotdByCustomerId(Long customerId, Pageable pageable);

    Page<Review> findReviewByCustomerId(Long customerId, Pageable pageable);

    // 추가적으로 게시물 타입에 따른 공통적인 메서드를 정의할 수 있습니다.
}
