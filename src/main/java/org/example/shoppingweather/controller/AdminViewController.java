package org.example.shoppingweather.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminViewController {

    // 미영님의 boardviewcontroller와 합칠 예정
    // productlist
    // @GetMapping("/product-list")
    // Public String productlist(){
    //      return "boardlist";
    // }

    @GetMapping("/product/upload")
    public String uploadProduct() {
        //상품 등록 폼으로 이동
        return "upload_product";
        // name : 상품 이름
        // price : 상품 가격
        // mainPicture : 대표 이미지
        // detailPicture : 상세 이미지
        // description : 상품 설명
        // quantity : 재고
        // category : 카테고리(겉옷, 상의, 하의, 악세서리)
        // season : 추천계절
        // temperature : 추천온도(범위 제시8)

        // *여름(23~) - 민소매, 반팔, 반바지, 치마
        // *봄가을(9~23) - 긴팔티, 자켓/코트(트렌치), 긴바지, 얇은니트/가디건, 맨투맨, 후드, 치마
        // *겨울(~9) - 패딩/코트, 기모/누빔(맨투맨, 후드, 바지), 가죽, 목도리/귀마개/히트텍etc, 두꺼운니트/가디건
    }


}
