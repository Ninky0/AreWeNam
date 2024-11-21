package org.example.shoppingweather.controller;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.example.shoppingweather.entity.Customer;
import org.example.shoppingweather.entity.Review;
import org.example.shoppingweather.service.CustomerService;
import org.example.shoppingweather.service.ReviewService;
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
    private final ReviewService reviewService;

    @GetMapping("/{productId}/{purchaseId}")
    public String writeReview(@PathVariable Long productId, @PathVariable Long purchaseId, Model model, HttpSession session) {
        Customer customer = customerService.findBySession(session);

        if (customer == null) {
            return "redirect:/user/login";
        }

        model.addAttribute("productId", productId);
        model.addAttribute("purchaseId", purchaseId);
        model.addAttribute("customerId", customer.getId());

        return "review_write";
    }

    @GetMapping("/detail/{productId}/{purchaseId}")
    public String detailReview(@PathVariable Long productId, @PathVariable Long purchaseId, Model model, HttpSession session) {
        Customer customer = customerService.findBySession(session);

        if (customer == null) {
            return "redirect:/user/login";
        }

        model.addAttribute("productId", productId);
        model.addAttribute("purchaseId", purchaseId);
        model.addAttribute("customerId", customer.getId());

        Review review = reviewService.findByIds(purchaseId, productId, customer.getId());
        model.addAttribute("review", review);

        return "detail_review";
    }

    @GetMapping("/edit/{productId}/{purchaseId}")
    public String editReview(@PathVariable Long productId, @PathVariable Long purchaseId, Model model, HttpSession session) {
        Customer customer = customerService.findBySession(session);

        if (customer == null) {
            return "redirect:/user/login";
        }

        model.addAttribute("productId", productId);
        model.addAttribute("purchaseId", purchaseId);
        model.addAttribute("customerId", customer.getId());

        Review review = reviewService.findByIds(purchaseId, productId, customer.getId());
        model.addAttribute("review", review);

        return "edit_review";
    }
}
