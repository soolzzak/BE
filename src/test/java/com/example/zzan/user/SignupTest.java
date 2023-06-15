package com.example.zzan.user;

import com.example.zzan.global.exception.ApiException;
import com.example.zzan.global.jwt.JwtUtil;
import com.example.zzan.user.dto.UserRequestDto;
import com.example.zzan.user.entity.Gender;
import com.example.zzan.user.entity.User;
import com.example.zzan.user.repository.UserRepository;
import com.example.zzan.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@ExtendWith(MockitoExtension.class)
class SignupTest {

    @Autowired
    UserService userService;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    UserRepository userRepository;

    @Mock
    HttpServletResponse response;

    @Mock
    HttpServletRequest request;

    private UserRequestDto userRequestDto;
    private Date birthday;

    @BeforeEach
    void signup() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            birthday = format.parse("1999-01-01");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        userRequestDto = new UserRequestDto("test@gmail.com", "asdf1234!", "tester", birthday, Gender.FEMALE, false, "");
        userService.signup(userRequestDto);
    }

    @Nested
    @DisplayName("TEST: 회원가입")
    class signup {
        @Test
        @DisplayName("CASE: 회원가입 성공")
        void signupTest() {
            UserRequestDto userRequestDto1 = new UserRequestDto("test1@gmail.com", "asdf1234!", "tester", birthday, Gender.FEMALE, false, "");
            ResponseEntity<?> signup = userService.signup(userRequestDto1);

            User user1 = userRepository.findByEmail(userRequestDto1.getEmail());
            Assertions.assertThat(user1).isNotNull();
            Assertions.assertThat(user1.getEmail()).isEqualTo(userRequestDto1.getEmail());
        }

        @Nested
        @DisplayName("CASE: 회원가입 실패")
        class signupFail {
            @Test
            @DisplayName("CASE: email 중복 회원가입 실패")
            void duplicateEmail() {
                UserRequestDto userRequestDto2 = new UserRequestDto("test@gmail.com", "asdf1234!", "tester", birthday, Gender.FEMALE, false, "");

                Assertions.assertThatThrownBy(() -> {
                    userService.signup(userRequestDto2);
                }, "EMAIL_DUPLICATION", ApiException.class);
            }
        }
    }
}