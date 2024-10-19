package org.example.shoppingweather.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.service.CustomerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MypageViewController {

    private final CustomerService customerService;

    @GetMapping
    public String mypage(HttpSession session, Model model) {
//        // 세션에서 loginId를 가져옴 (String으로 캐스팅)
//        String loginId = (String) session.getAttribute("loginId");
//
//        // loginId로 고객 정보를 데이터베이스에서 조회
//        Customer customer = customerRepository.findByLoginId(loginId);
//
//        // 모델에 고객 정보를 추가
//        model.addAttribute("customer", customer);

        // 위의 과정을 한줄로
        // 비즈니스 로직을 서비스 계층에 몰아넣고, 컨트롤러는 단순히 요청을 처리 및 응답만.
        model.addAttribute("customer", customerService.findBySession(session));

        // 고객의 ID 출력
        //System.out.println(customer.getId());

        return "mypage";
    }

    @GetMapping("/edit")
    public String update(HttpSession session, Model model) {
        model.addAttribute("customer", customerService.findBySession(session));

        return "update"; // 뷰 이름 반환
    }

    @GetMapping("/quitout")
    public String quitout(HttpSession session, Model model) {
        model.addAttribute("customer", customerService.findBySession(session));

        return "quit";
    }

    @GetMapping("/history")
    public String history(HttpSession session, Model model) {
        model.addAttribute("customer", customerService.findBySession(session));

        // 구매 목록
        return "history";
    }

    @GetMapping("/cart")
    public String cart(HttpSession session, Model model) {
        model.addAttribute("customer", customerService.findBySession(session));

        // 장바구니
        return "cart";
    }

}
