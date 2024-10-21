package org.example.shoppingweather.controller;

import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.dto.product.ProdReadResponseDTO;
import org.example.shoppingweather.service.AdminService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminViewController {

    private final AdminService adminService;

    @GetMapping("/product/list")
    public String productList(Model model){
        List<ProdReadResponseDTO> products = adminService.findAll();
        model.addAttribute("products", products);
        return "product_list";
    }

    @GetMapping("/product/upload")
    public String uploadProduct() {
        //상품 등록 폼으로 이동
        return "upload_product";

        // *여름(23~) - 민소매, 반팔, 반바지, 치마
        // *봄가을(9~23) - 긴팔티, 자켓/코트(트렌치), 긴바지, 얇은니트/가디건, 맨투맨, 후드, 치마
        // *겨울(~9) - 패딩/코트, 기모/누빔(맨투맨, 후드, 바지), 가죽, 목도리/귀마개/히트텍etc, 두꺼운니트/가디건
    }
    // admin 상품 상세 정보 매핑 추가
    @GetMapping("/product/detail_product/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        ProdReadResponseDTO product = adminService.findById(id); // id로 상품 정보 찾기
        model.addAttribute("product", product);
        return "detail_product"; // 상세 페이지 HTML 파일 이름
    }
    // customer 상품 상세 정보 매핑 추가
    @GetMapping("/product/detail/{id}")
    public String detail(@PathVariable Long id, Model model) {
        // id로 상품 정보 찾기
        ProdReadResponseDTO product = adminService.findById(id);

        // mainPicture 경로에서 역슬래시(`\`)를 슬래시(`/`)로 변경
        if (product.getMainPicture() != null) {
            String mainPicturePath = product.getMainPicture().replace("\\", "/");
            product.setMainPicture(mainPicturePath); // 경로 수정 후 다시 설정
        }

        // 수정된 product 객체를 모델에 추가
        model.addAttribute("product", product);

        // 상세 페이지 HTML 파일로 반환
        return "detail";
    }

    @GetMapping("/product/edit/{id}")
    public String editProduct(@PathVariable Long id, Model model) {
        // 상품의 상세 정보를 조회하여 수정 폼에 표시할 수 있도록 모델에 추가
        ProdReadResponseDTO product = adminService.findById(id);
        model.addAttribute("product", product);
        return "edit_product"; // 수정 페이지 HTML 파일 이름
    }

}
