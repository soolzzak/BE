package com.example.zzan.service;

import com.example.zzan.dto.TokenDto;
import com.example.zzan.dto.UserRequestDto;
import com.example.zzan.dto.UserloginDto;
import com.example.zzan.entity.RefreshToken;
import com.example.zzan.entity.StatusEnum;
import com.example.zzan.entity.User;
import com.example.zzan.entity.UserRole;
import com.example.zzan.exception.ApiException;
import com.example.zzan.exception.ExceptionEnum;
import com.example.zzan.exception.Message;
import com.example.zzan.repository.RefreshTokenRepository;
import com.example.zzan.repository.UserRepository;
import com.example.zzan.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static com.example.zzan.entity.UserRole.USER;
import static com.example.zzan.util.JwtUtil.ACCESS_KEY;
import static com.example.zzan.util.JwtUtil.REFRESH_KEY;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private static final String ADMIN_TOKEN = "adminToken";
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ResponseEntity<Message> signup(UserRequestDto requestDto) {
        String userName = requestDto.getUserName();
        String userEmail = requestDto.getEmail();
        String userPassword = passwordEncoder.encode(requestDto.getPassword());
        String userRole = requestDto.getAdmin();

//        Optional<User> found = userRepository.findUserByEmail(userEmail);
//
//        if (found.isPresent()) {
////            return new ResponseDto("아이디 중복", HttpStatus.BAD_REQUEST);
//        }

        UserRole role = USER;
//        if (userRole.equals("admin")) {
//            if (!ADMIN_TOKEN.equals(requestDto.getAdminToken())) {
////                return new ResponseDto("토큰값이 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
//            }
//            role =  UserRole.ADMIN;
//        }else{
//            role =  UserRole.USER;
//        }

        User user = new User(userEmail, userPassword,userName, role);

        userRepository.save(user);

        Message message = Message.setSuccess(StatusEnum.OK, "회원가입성공");
        return new ResponseEntity<>(message, HttpStatus.OK);
    }


    @Transactional
    public ResponseEntity<Message> login(UserloginDto requestDto, HttpServletResponse response) {

        String userEmail = requestDto.getEmail();
        String userPassword = requestDto.getPassword();

        try {
            User user = userRepository.findUserByEmail(userEmail).orElseThrow(
                    () -> new ApiException(ExceptionEnum.NOT_FOUND_USER)
//                    () -> new IllegalArgumentException("없는 이메일 입니다.")
            );

            // 비밀번호 확인
            if(!passwordEncoder.matches(userPassword, user.getPassword())){
                throw new ApiException(ExceptionEnum.BAD_REQUEST);
//                return new ResponseDto("비밀번호를 확인해주세요!!", HttpStatus.BAD_REQUEST);
            }

            //username (ID) 정보로 Token 생성
            TokenDto tokenDto = jwtUtil.createAllToken(requestDto.getEmail(), user.getAdmin());

            //Refresh 토큰 있는지 확인
            Optional<RefreshToken> refreshToken = refreshTokenRepository.findRefreshTokenByUserEmail(requestDto.getEmail());

            //Refresh 토큰이 있다면 새로 발급 후 업데이트
            //없다면 새로 만들고 DB에 저장
            if (refreshToken.isPresent()) {
                RefreshToken savedRefreshToken = refreshToken.get();
                RefreshToken updateToken = savedRefreshToken.updateToken(tokenDto.getRefreshToken().substring(7));
                refreshTokenRepository.save(updateToken);
            } else {
                RefreshToken newToken = new RefreshToken(tokenDto.getRefreshToken().substring(7), userEmail);
                refreshTokenRepository.save(newToken);
            }

            //응답 헤더에 토큰 추가
            setHeader(response, tokenDto, user.getEmail());
            Message message = Message.setSuccess(StatusEnum.OK, "로그인 성공");

            return new ResponseEntity<>(message, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    private void setHeader(HttpServletResponse response, TokenDto tokenDto, String userEmail) {
        response.addHeader(ACCESS_KEY, tokenDto.getAccessToken());
        response.addHeader(REFRESH_KEY, tokenDto.getRefreshToken());
        response.addHeader("USER_EMAIL", userEmail);
    }


    @Transactional
    public ResponseEntity<Message> logout(String userEmail) {
        RefreshToken refreshToken = refreshTokenRepository.findRefreshTokenByUserEmail(userEmail)
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_REFRESH_TOKEN)
                );
        refreshTokenRepository.delete(refreshToken);
        Message message = Message.setSuccess(StatusEnum.OK, "로그 아웃");
        return new ResponseEntity<>(message, HttpStatus.OK);
    }
}

