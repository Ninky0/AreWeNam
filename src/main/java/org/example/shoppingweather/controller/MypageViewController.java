package org.example.shoppingweather.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.entity.Customer;
import org.example.shoppingweather.repository.CustomerRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MypageViewController {

    private final CustomerRepository customerRepository;

    @GetMapping
    public String mypage(HttpSession session, Model model) {
        String loginId = (String) session.getAttribute("loginId");
        Customer customer = customerRepository.findByLoginId(loginId);
        model.addAttribute("customer", customer);

        return "mypage";
    }

    @GetMapping("/update")
    public String update(HttpSession session, Model model) {
        // 세션에서 loginId를 가져옴 (String으로 캐스팅)
        String loginId = (String) session.getAttribute("loginId");

        // loginId로 고객 정보를 데이터베이스에서 조회
        Customer customer = customerRepository.findByLoginId(loginId);

        // 모델에 고객 정보를 추가
        model.addAttribute("customer", customer);

        // 고객의 ID 출력
        System.out.println(customer.getId());

        return "update"; // 뷰 이름 반환
    }

}
