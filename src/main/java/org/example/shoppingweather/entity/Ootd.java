package org.example.shoppingweather.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.shoppingweather.dto.OotdWriteRequestDTO;

import java.time.LocalDateTime;

@Table(name = "ootd") // 테이블 이름을 "ootd"로 설정
@NoArgsConstructor(access = AccessLevel.PUBLIC) // 기본 생성자의 접근 수준을 PUBLIC으로 설정하여 외부에서 인스턴스를 생성 가능하도록 함
@Getter
@Setter
@Entity
public class Ootd {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID 자동 생성
    private Long id;  // OOTD 고유 식별자
    private Long productId; // 단일 상품 ID 필드

    @ManyToOne
    @JoinColumn(name = "customerId", nullable = false)  // customerId로 수정
    private Customer customer;

    @Column(nullable = false)
    private LocalDateTime date; // 작성 날짜 및 시간

    @Column(nullable = false)
    private String picture; // 이미지 경로를 저장

    private String tag; // 태그 정보

    public static Ootd fromDTO(OotdWriteRequestDTO requestDTO, String picturePath, Customer customer) {
        Ootd ootd = new Ootd();
        ootd.setProductId(requestDTO.getProductId());
        ootd.setCustomer(customer);
        ootd.setDate(LocalDateTime.now());
        ootd.setPicture(picturePath);
        ootd.setTag(requestDTO.getTag());
        return ootd;
    }

    @PrePersist
    protected void onCreate() {
        this.date = LocalDateTime.now();  // 엔티티가 생성될 때 자동으로 현재 날짜 및 시간을 설정
    }
}
