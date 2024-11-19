package org.example.shoppingweather.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.entity.Customer;
import org.example.shoppingweather.entity.Purchase;
import org.example.shoppingweather.service.CustomerService;
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
        Customer customer = customerService.findBySession(session);
        if (customer == null) {
            return "redirect:/login"; // 고객이 로그인하지 않았다면 로그인 페이지로 리다이렉트
        }

        model.addAttribute("customer", customer);

        List<Purchase> purchases = purchaseService.getPurchasesByCustomer(customer);
        purchaseService.enrichPurchasesWithProducts(purchases);

        model.addAttribute("purchases", purchases);

        return "purchaselist";
    }


    @GetMapping("/post")
    public String post(HttpSession session, Model model) {
        model.addAttribute("customer", customerService.findBySession(session));
        // 게시글 목록
        return "customer_post";
    }
}
