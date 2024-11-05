package org.example.shoppingweather.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.entity.Customer;
import org.example.shoppingweather.entity.Product;
import org.example.shoppingweather.entity.Purchase;
import org.example.shoppingweather.service.CustomerService;
import org.example.shoppingweather.service.ProductService;
import org.example.shoppingweather.service.PurchaseService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MypageViewController {

    private final CustomerService customerService;
    private final PurchaseService purchaseService;
    private final ProductService productService;

    @GetMapping
    public String mypage(HttpSession session, Model model) {
        // 현재 사용자의 인증 정보를 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 권한(Role)에 따라 다른 페이지로 분기 처리
        if (authentication != null) {
            // 세션에서 Customer 객체를 가져옴
            Customer customer = customerService.findBySession(session);

            model.addAttribute("customer", customer); // Customer 엔티티를 모델에 추가

            // 권한(Role)에 따라 다른 페이지로 분기 처리
            if (authentication != null && authentication.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
                return "adminpage";
            } else if (authentication != null && authentication.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_CUSTOMER"))) {
                // 고객인 경우 고객 마이페이지로 이동
                model.addAttribute("customer", customerService.findBySession(session));
                return "customerpage";
            }
        }

        // 인증되지 않은 사용자는 로그인 페이지로 리다이렉트
        return "redirect:/user/login";
    }


//        // 세션에서 loginId를 가져옴 (String으로 캐스팅)
//        String loginId = (String) session.getAttribute("loginId");
//
//        // loginId로 고객 정보를 데이터베이스에서 조회
//        Customer customer = customerRepository.findByLoginId(loginId);
//
//        // 모델에 고객 정보를 추가
//        model.addAttribute("customer", customer);
//
//        // 위의 과정을 한줄로
//        // 비즈니스 로직을 서비스 계층에 몰아넣고, 컨트롤러는 단순히 요청을 처리 및 응답만.
//        model.addAttribute("customer", customerService.findBySession(session));
//
//        // 고객의 ID 출력
//        //System.out.println(customer.getId());
//
//        return "mypage";


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
        // 세션에서 고객정보 모델에 추가
        model.addAttribute("customer", customerService.findBySession(session));

        // 고객 구매 목록 가져오기
        Customer customer = (Customer) model.getAttribute("customer");
        List<Purchase> purchases = purchaseService.getPurchasesByCustomer(customer);

        // 구매 목록 제품 정보 추출
        List<Product> products = purchaseService.extractProductsFromPurchases(purchases);

        model.addAttribute("purchases", purchases); // 모델에 구매 목록 추가
        model.addAttribute("products", products); // 모델에 제품 목록 추가

        // 구매 목록
        return "purchaselist";
    }


    @GetMapping("/post")
    public String post(HttpSession session, Model model) {
        model.addAttribute("customer", customerService.findBySession(session));
        // 게시글 목록
        return "customer_post";
    }
}
