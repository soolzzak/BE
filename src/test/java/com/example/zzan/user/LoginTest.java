package com.example.zzan.user;

import com.example.zzan.global.exception.ApiException;
import com.example.zzan.global.jwt.JwtUtil;
import com.example.zzan.user.dto.UserLoginDto;
import com.example.zzan.user.entity.User;
import com.example.zzan.user.repository.UserRepository;
import com.example.zzan.user.service.UserService;

import jakarta.servlet.http.HttpServletResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@ExtendWith(MockitoExtension.class)
class LoginTest {

    @Autowired
    UserService userService;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    UserRepository userRepository;

    @Mock
    HttpServletResponse response;

    private User user;

    @BeforeEach
    void login() {
        user = new User();
        user.setEmail("test@gmail.com");
        user.setPassword("encodedPassword"); // Replace with the actual encoded password
        userRepository.save(user);
    }

    @Nested
    @DisplayName("TEST: 로그인")
    class login {
        @Test
        @DisplayName("CASE: 로그인 성공")
        void loginTest() {
            UserLoginDto loginDto = new UserLoginDto("test@gmail.com", "password");
            ResponseEntity<?> responseEntity = userService.login(loginDto, response);

            Assertions.assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200);
            // Add more assertions as needed
        }

        @Nested
        @DisplayName("CASE: 로그인 실패")
        class loginFail {
            @Test
            @DisplayName("CASE: 잘못된 비밀번호로 로그인 실패")
            void wrongPassword() {
                UserLoginDto loginDto = new UserLoginDto("test@gmail.com", "wrongPassword");

                Assertions.assertThatThrownBy(() -> {
                    userService.login(loginDto, response);
                }).isInstanceOf(ApiException.class);
            }

            @Test
            @DisplayName("CASE: 존재하지 않는 이메일로 로그인 실패")
            void nonExistingEmail() {
                UserLoginDto loginDto = new UserLoginDto("nonexisting@gmail.com", "password");

                Assertions.assertThatThrownBy(() -> {
                    userService.login(loginDto, response);
                }).isInstanceOf(ApiException.class);
            }
        }
    }
}

