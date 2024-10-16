package org.example.shoppingweather.controller;

import jakarta.servlet.http.HttpSession;
import org.example.shoppingweather.entity.Customer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.swing.text.html.parser.Entity;

@Controller
public class MypageController {

    @GetMapping("/mypage")
    public String mypage() {
        return "mypage";
    }

    @GetMapping("/update")
    public String update() {
        return "update";
    }

}
