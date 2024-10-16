package org.example.shoppingweather.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BoardViewController {
    @GetMapping("/product_list")
    public String product_list(){
        return "productList";
    }
}
