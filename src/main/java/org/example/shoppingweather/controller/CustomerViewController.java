package org.example.shoppingweather.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.example.shoppingweather.dto.Customer.CustomerCartResponseDTO;
import org.example.shoppingweather.entity.Cart;
import org.example.shoppingweather.entity.Customer;
import org.example.shoppingweather.entity.Product;
import org.example.shoppingweather.repository.CartRepository;
import org.example.shoppingweather.service.CartService;
import org.example.shoppingweather.service.CustomerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@AllArgsConstructor
@RequestMapping("/user")
public class CustomerViewController {

    private final CustomerService customerService;
    private final CartRepository cartRepository;
    private final CartService cartService;


    @GetMapping("/join")
    public String signUp() {
        return "join";
    }

    @GetMapping("/login")
    public String signIn() {
        return "login";
    }

    @GetMapping("/shoppingcart")
    public String cart(HttpSession session, Model model) {
        // 세션에서 고객 정보 가져오기
        Customer customer = customerService.findBySession(session);
        model.addAttribute("customer", customer);

        // 고객의 장바구니를 가져오기
        Optional<Cart> optionalCart = cartRepository.findByCustomerId(customer.getId());
        if (optionalCart.isPresent()) {
            Cart cart = optionalCart.get();

            // 장바구니의 상품 목록과 함께 수량 정보를 모델에 추가
            List<Product> products = cartService.getProductsFromCart(cart);
            model.addAttribute("products", products);
            model.addAttribute("cart", cart); // cart 객체를 추가하여 수량 정보 접근 가능
        } else {
            // 장바구니가 비어있는 경우 처리
            model.addAttribute("products", new ArrayList<>()); // 빈 리스트 추가
        }

        return "shoppingcart"; // 뷰 이름
    }

    @GetMapping("/shoppingcart/ordercomplete")
    public String ordercomplete() {
        return "ordercomplete";
    }

}