package org.example.shoppingweather.controller;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.example.shoppingweather.entity.Customer;
import org.example.shoppingweather.service.CustomerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
@RequestMapping("/review")
public class ReviewViewController {
    private final CustomerService customerService;

    @GetMapping("/{productId}/write")
    public String writeReview(@PathVariable Long productId, Model model, HttpSession session) {
        Customer customer = customerService.findBySession(session);

        if (customer != null) {
            model.addAttribute("customerId", customer.getId());
        } else {
            return "redirect:/user/login";
        }

        model.addAttribute("productId", productId);
        model.addAttribute("customerId", customer.getId());

        return "review_write";
    }

}
