package org.example.shoppingweather.service;

import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.dto.Customer.CustomerOotdImageResponseDTO;
import org.example.shoppingweather.dto.OotdWriteRequestDTO;
import org.example.shoppingweather.entity.Customer;
import org.example.shoppingweather.entity.Ootd;
import org.example.shoppingweather.entity.Product;
import org.example.shoppingweather.repository.CustomerRepository;
import org.example.shoppingweather.repository.OotdRepository;
import org.example.shoppingweather.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OotdService {
    private final OotdRepository ootdRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    // OOTD 게시글의 이미지 목록을 DTO로 가져오는 메서드
    public Page<CustomerOotdImageResponseDTO> getOotdImages(Pageable pageable) {
        Page<Ootd> ootdPage = ootdRepository.findAll(pageable); // OOTD 엔티티 페이지 가져오기

        // OOTD 엔티티를 DTO로 변환
        return ootdPage.map(ootd -> new CustomerOotdImageResponseDTO(ootd.getPicture()));
    }

    // OOTD 게시글 저장 기능 추가
    public void saveOotdPost(OotdWriteRequestDTO requestDTO, String picturePath) {
        Customer customer = customerRepository.findById(requestDTO.getCustomerId()).orElseThrow(() -> new IllegalArgumentException("Invalid customer ID"));
        Ootd ootd = Ootd.fromDTO(requestDTO, picturePath, customer);

        ootdRepository.save(ootd); // OOTD 데이터 저장
    }
}