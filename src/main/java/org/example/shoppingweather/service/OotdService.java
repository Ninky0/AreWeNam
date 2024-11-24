package org.example.shoppingweather.service;

import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.dto.ootd.CustomerOotdImageResponseDTO;
import org.example.shoppingweather.dto.ootd.OotdWriteRequestDTO;
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
        Page<Ootd> ootdPage = ootdRepository.findAll(pageable);

        // OOTD 엔티티를 DTO로 변환
        return ootdPage.map(ootd -> new CustomerOotdImageResponseDTO(
                ootd.getId(),
                ootd.getPicture(),
                ootd.getTag(),
                ootd.getDate(),
                ootd.getProduct() != null ? ootd.getProduct().getId() : null,
                ootd.getCustomer().getLoginId(),
                ootd.getProduct() != null ? ootd.getProduct().getName() : "Unknown"
        ));
    }

    // 모달창 안의 이미지
    public Page<CustomerOotdImageResponseDTO> getOotdModalImage(Pageable pageable) {
        Page<Ootd> ootdPage = ootdRepository.findAll(pageable);
        return ootdPage.map(ootd -> {
            CustomerOotdImageResponseDTO responseDTO = new CustomerOotdImageResponseDTO();
            responseDTO.setId(ootd.getId()); // OOTD ID 설정
            responseDTO.setPicture(ootd.getPicture()); // 이미지 경로 설정
            // 필요한 필드가 있으면 추가로 설정
            return responseDTO;
        });
    }

    // OOTD 게시글 저장 기능 추가
    public void saveOotdPost(OotdWriteRequestDTO requestDTO, String picturePath) {
        Customer customer = customerRepository.findById(requestDTO.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid customer ID"));

        Product product = productRepository.findById(requestDTO.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID"));

        Ootd ootd = Ootd.fromDTO(requestDTO, picturePath, customer, product);

        ootdRepository.save(ootd); // OOTD 데이터 저장
    }

    public Page<CustomerOotdImageResponseDTO> getOotdPostsByCustomerId(Long customerId, Pageable pageable) {
        return ootdRepository.findByCustomerId(customerId, pageable)
                .map(ootd -> new CustomerOotdImageResponseDTO(
                        ootd.getId(),
                        ootd.getPicture(),
                        ootd.getTag(),
                        ootd.getDate(),
                        ootd.getProduct() != null ? ootd.getProduct().getId() : null,
                        ootd.getCustomer().getLoginId(),
                        ootd.getProduct() != null ? ootd.getProduct().getName() : "Unknown"
                ));
    }


    public CustomerOotdImageResponseDTO findOotdById(Long id) {
        Ootd ootd = ootdRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No OOTD found for the given ID"));

        // Create the DTO and populate fields from Ootd
        CustomerOotdImageResponseDTO responseDTO = new CustomerOotdImageResponseDTO();
        responseDTO.setId(ootd.getId());
        responseDTO.setPicture(ootd.getPicture());
        responseDTO.setTag(ootd.getTag());
        responseDTO.setProductId(ootd.getProduct() != null ? ootd.getProduct().getId() : null);

        // Fetch and add product information if Product is associated
        if (ootd.getProduct() != null) {
            Product product = ootd.getProduct();
            responseDTO.setProductPrice(String.valueOf(product.getProductPrice()));
            responseDTO.setProductCategory(product.getProductCategory());
            responseDTO.setProductSeason(product.getProductSeason());
            responseDTO.setProductTemperature(product.getProductTemperature());
            responseDTO.setMainPicturePath(product.getMainPicturePath().replace("\\", "/")); // Clean the path
        }

        return responseDTO;
    }
}
