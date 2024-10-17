package org.example.shoppingweather.controller;

import jakarta.servlet.http.HttpSession;
import org.example.shoppingweather.entity.Customer;
import org.example.shoppingweather.repository.CustomerRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.swing.text.html.parser.Entity;

@Controller
public class MypageController {

    private final CustomerRepository customerRepository;

    public MypageController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GetMapping("/mypage")
    public String mypage(HttpSession session, Customer customer, Model model) {
        setSession(session, customer);

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


    private void setSession(HttpSession session, Customer customer) {
        String loginId = (String) session.getAttribute("loginId");
        Customer customer1 = customerRepository.findByLoginId(loginId);
        String password = customer1.getPassword();
        String name = customer1.getName();
        String email = customer1.getEmail();
        String phone = customer1.getPhone();
        String address = customer1.getAddress();

//        String password = (String) session.getAttribute("password");
//        String name = (String) session.getAttribute("name");
//        String email = (String) session.getAttribute("email");
//        String phone = (String) session.getAttribute("phone");
//        String address = (String) session.getAttribute("address");

        customer.setLoginId(loginId);
        customer.setPassword(password);
        customer.setName(name);
        customer.setEmail(email);
        customer.setPhone(phone);
        customer.setAddress(address);
    }

}
