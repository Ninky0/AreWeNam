package org.example.shoppingweather.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.dto.ootd.CustomerOotdImageResponseDTO;
import org.example.shoppingweather.entity.Customer;
import org.example.shoppingweather.service.CustomerService;
import org.example.shoppingweather.service.OotdService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/ootd")
public class OotdViewController {

    private final CustomerService customerService;
    private final OotdService ootdService;

    @GetMapping("/list")
    public String ootdList(Model model, Pageable pageable) {
        Page<CustomerOotdImageResponseDTO> ootdImages = ootdService.getOotdImages(pageable);
        model.addAttribute("ootdImages", ootdImages);
        return "ootd_list";
    }

    @GetMapping("/write")
    public String ootdWrite(Model model, HttpSession session) {
        Customer customer = customerService.findBySession(session);
        if (customer != null) {
            model.addAttribute("customerId", customer.getId());
        } else {
            return "redirect:/user/login";
        }
        return "ootd_write";
    }

}
