package org.example.shoppingweather.service;

import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.dto.Customer.CustomerOotdImageResponseDTO;
import org.example.shoppingweather.entity.Customer;
import org.example.shoppingweather.entity.Ootd;
import org.example.shoppingweather.repository.CustomerRepository;
import org.example.shoppingweather.repository.OotdRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OotdService {
    private final OotdRepository ootdRepository;
    private final CustomerRepository customerRepository;

    // OOTD 게시글의 이미지 목록을 DTO로 가져오는 메서드
    public Page<CustomerOotdImageResponseDTO> getOotdImages(Pageable pageable) {
        Page<Ootd> ootdPage = ootdRepository.findAll(pageable); // OOTD 엔티티 페이지 가져오기

        // OOTD 엔티티를 DTO로 변환
        return ootdPage.map(ootd -> new CustomerOotdImageResponseDTO(ootd.getPicture()));
    }

    // OOTD 게시글 저장 기능 추가
    public void saveOotdPost(Long customerId, String tag, String picturePath, Long productId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid customer ID: " + customerId));

        Ootd ootd = new Ootd();
        ootd.setCustomer(customer); // 고객 정보 설정
        ootd.setPicture(picturePath); // 이미지 경로 설정
        ootd.setTag(tag); // 태그 설정
        ootd.setProductId(productId); // 선택된 상품 ID 설정

        ootdRepository.save(ootd); // OOTD 데이터 저장
    }
}
