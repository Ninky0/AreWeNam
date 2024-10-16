package org.example.shoppingweather.service;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.dto.CustomerUpdateRequestDTO;
import org.example.shoppingweather.dto.sign.SignUpRequestDTO;
import org.example.shoppingweather.entity.Customer;
import org.example.shoppingweather.dto.sign.SignInResponseDTO;
import org.example.shoppingweather.mapper.CustomerMapper;
import org.example.shoppingweather.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerMapper customerMapper;

    private final CustomerRepository customerRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public Long save(SignUpRequestDTO dto) {
        return customerRepository.save(Customer.builder()
                .loginId(dto.getLoginId())  // dto 필드명에 맞춰 수정
                .password(bCryptPasswordEncoder.encode(dto.getPassword()))
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .build()).getId();
    }

    public void updateUser(Long id, CustomerUpdateRequestDTO dto, String  loggedInCustomerId) {
        if (!id.equals(loggedInCustomerId)) {
            throw new RuntimeException("You are not authorized to update this user.");
        }

        // 고객을 데이터베이스에서 찾기
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));

        // 고객 정보 업데이트
        customerRepository.save(Customer.builder()
                // dto 필드명에 맞춰 수정
                .password(bCryptPasswordEncoder.encode(dto.getPassword()))
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .build()).getId();
    }

    public SignInResponseDTO signIn(Customer customer, HttpSession session) {
        // 로그인 ID로 고객 정보 조회
        Customer getCustomer = customerMapper.signIn(customer.getLoginId()); // login_id를 사용하여 조회

        if (getCustomer == null) {
            return makeSignInRequestDTO(false, "존재하지 않는 회원입니다.", null, null);
        }

        if (!customer.getPassword().equals(getCustomer.getPassword())) {
            return makeSignInRequestDTO(false, "비밀번호가 틀렸습니다.", null, null);
        }

        // 세션 설정
        session.setAttribute("loginId", getCustomer.getLoginId());
        session.setAttribute("userName", getCustomer.getName());

        return makeSignInRequestDTO(true, "로그인이 성공했습니다.", "/", getCustomer);  // 세션 설정 후 getCustomer를 전달
    }

    private SignInResponseDTO makeSignInRequestDTO(boolean isloggedIn, String message, String url, Customer customer) {
        return SignInResponseDTO.builder()
                .isLoggedIn(isloggedIn)
                .message(message)
                .url(url)
                .loginId(isloggedIn ? customer.getLoginId() : null)  // login_id를 사용
                .name(isloggedIn ? customer.getName() : null)
                .build();
    }

}
