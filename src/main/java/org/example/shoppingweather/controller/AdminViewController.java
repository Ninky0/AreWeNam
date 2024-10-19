package org.example.shoppingweather.controller;

import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.dto.product.ProdReadResponseDTO;
import org.example.shoppingweather.service.AdminService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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


}
