package org.example.shoppingweather.service;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.entity.Customer;
import org.example.shoppingweather.dto.SignInResponseDTO;
import org.example.shoppingweather.mapper.CustomerMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerMapper customerMapper;

    public void signUp(Customer customer) {
        customerMapper.signUp(customer);
    }

    public SignInResponseDTO signIn(Customer customer, HttpSession session) {
        // 로그인 ID로 고객 정보 조회
        Customer getCustomer = customerMapper.signIn(customer.getLogin_id()); // login_id를 사용하여 조회

        if (getCustomer == null) {
            return makeSignInRequestDTO(false, "존재하지 않는 회원입니다.", null, null);
        }

        if (!customer.getPassword().equals(getCustomer.getPassword())) {
            return makeSignInRequestDTO(false, "비밀번호가 틀렸습니다.", null, null);
        }

        // 세션 설정
        session.setAttribute("userId", getCustomer.getLogin_id());
        session.setAttribute("userName", getCustomer.getName());

        return makeSignInRequestDTO(true, "로그인이 성공했습니다.", "/", getCustomer);  // 세션 설정 후 getCustomer를 전달
    }

    private SignInResponseDTO makeSignInRequestDTO(boolean isloggedIn, String message, String url, Customer customer) {
        return SignInResponseDTO.builder()
                .isLoggedIn(isloggedIn)
                .message(message)
                .url(url)
                .login_id(isloggedIn ? customer.getLogin_id() : null)  // login_id를 사용
                .name(isloggedIn ? customer.getName() : null)
                .build();
    }
}
