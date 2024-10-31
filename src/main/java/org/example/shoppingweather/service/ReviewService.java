package org.example.shoppingweather.service;

import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.dto.Customer.CustomerReviewResponseDTO;
import org.example.shoppingweather.entity.Review;
import org.example.shoppingweather.repository.ReviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public Page<CustomerReviewResponseDTO> findReviewsByProductId(Long productId, Pageable pageable) {
        // productId를 기반으로 리뷰를 찾는 리포지토리 메소드 호출
        Page<Review> reviewPage = reviewRepository.findByProductId(productId, pageable);

        // Review 엔티티를 CustomerReviewResponseDTO로 변환
        return reviewPage.map(this::convertToDto);
    }

    private CustomerReviewResponseDTO convertToDto(Review review) {
        return CustomerReviewResponseDTO.builder()
                .id(review.getId())
                .picturePath(review.getPicturePath())
                .content(review.getContent())
                .product(review.getProduct())
                .customer(review.getCustomer())
                .build();
    }
}
