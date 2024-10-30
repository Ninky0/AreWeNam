package org.example.shoppingweather.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.dto.UrlResponseDTO;
import org.example.shoppingweather.dto.sign.SignUpRequestDTO;
import org.example.shoppingweather.entity.Customer;
import org.example.shoppingweather.service.CustomerService;
import org.example.shoppingweather.service.WeatherService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class CustomerApiController {

    private final CustomerService customerService;
    private final WeatherService weatherService;

    @PostMapping("/join")
    public ResponseEntity<UrlResponseDTO> signup(@RequestBody SignUpRequestDTO signUpRequestDTO) {
        customerService.save(signUpRequestDTO); // 회원가입 진행 (DB 저장)
        return ResponseEntity.ok(
                UrlResponseDTO.builder()
                        .url("/user/login") // 회원 가입이 완료된 후 로그인 페이지로 이동
                        .build()
        );
    }

    @PostMapping("/shoppingcart")
    public ResponseEntity<UrlResponseDTO> addCart(@RequestBody Map<String, Object> payload) {
        try {
            if (payload.get("customerId") == null || payload.get("productId") == null || payload.get("quantity") == null) {
                return ResponseEntity.badRequest().body(
                        UrlResponseDTO.builder().message("요청에 필요한 모든 값을 포함해야 합니다.").build()
                );
            }

            Long customerId = Long.parseLong(payload.get("customerId").toString());
            Long productId = Long.parseLong(payload.get("productId").toString());
            Integer quantity = Integer.parseInt(payload.get("quantity").toString());

            customerService.addProductToCart(customerId, productId, quantity);
            return ResponseEntity.ok(
                    UrlResponseDTO.builder().message("상품이 장바구니에 추가되었습니다. 장바구니를 확인하시겠습니까?").build()
            );
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    UrlResponseDTO.builder().message("입력 형식이 올바르지 않습니다.").build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    UrlResponseDTO.builder().message("상품 추가 중 오류가 발생했습니다.").build()
            );
        }
    }

    @PostMapping("/ootd_write")
    public ResponseEntity<Map<String, String>> createPost(
            HttpSession session,
            @RequestParam("tag") String tag,
            @RequestParam("picture") MultipartFile pictureFile,
            @RequestParam("productId") Long productId) {

        Map<String, String> response = new HashMap<>();

        try {
            // 로그인된 사용자 확인
            String loginId = (String) session.getAttribute("loginId");
            if (loginId == null) {
                response.put("message", "로그인이 필요합니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // 세션에서 사용자 가져오기
            Customer customer = customerService.findBySession(session);
            Long customerId = customer.getId();

            // 이미지 경로 설정
            String picturePath = null;
            if (pictureFile != null && !pictureFile.isEmpty()) {
                String fileExtension = pictureFile.getOriginalFilename().substring(pictureFile.getOriginalFilename().lastIndexOf("."));
                String fileName = "picture_" + System.currentTimeMillis() + fileExtension;
                Path savePath = Paths.get("src/main/resources/static/uploads/", fileName);

                Files.createDirectories(savePath.getParent());
                Files.copy(pictureFile.getInputStream(), savePath);
                picturePath = "/uploads/" + fileName;
            }

            // OOTD 게시물 데이터와 이미지 경로를 저장
            customerService.saveOotdPost(customerId, tag, picturePath, productId);

            response.put("url", "/user/ootd_list");
            response.put("message", "상품 등록이 완료되었습니다.");
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            e.printStackTrace();
            response.put("message", "이미지 업로드 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
