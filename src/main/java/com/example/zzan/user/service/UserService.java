package com.example.zzan.user.service;

import com.example.zzan.global.dto.ResponseDto;
import com.example.zzan.global.exception.ApiException;
import com.example.zzan.global.security.dto.TokenDto;
import com.example.zzan.global.security.entity.RefreshToken;
import com.example.zzan.global.security.repository.RefreshTokenRepository;
import com.example.zzan.global.util.BadWords;
import com.example.zzan.global.util.JwtUtil;
import com.example.zzan.user.dto.UserLoginDto;
import com.example.zzan.user.dto.UserRequestDto;
import com.example.zzan.user.entity.User;
import com.example.zzan.user.entity.UserRole;
import com.example.zzan.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.example.zzan.global.exception.ExceptionEnum.*;
import static com.example.zzan.global.util.JwtUtil.ACCESS_KEY;
import static com.example.zzan.global.util.JwtUtil.REFRESH_KEY;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    private static final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";


    @Transactional
    public ResponseEntity signup(UserRequestDto requestDto) {
        String username = requestDto.getUsername();
        String userEmail = requestDto.getEmail();
        String userPassword = passwordEncoder.encode(requestDto.getPassword());

        Optional<User> found = userRepository.findUserByEmail(userEmail);
        if (found.isPresent()) {
          return ResponseEntity.badRequest().body(new ApiException(USERS_DUPLICATION));
        }

        if (hasBadWord(username)) {
            String errorMessage = "아이디에 사용할 수 없는 단어가 있습니다.";
            return ResponseEntity.badRequest().body(ResponseDto.setBadRequest(errorMessage));
        }

        UserRole role = requestDto.isAdmin() ? UserRole.ADMIN : UserRole.USER;
        if (role == UserRole.ADMIN && !ADMIN_TOKEN.equals(requestDto.getAdminKey())) {
            throw new ApiException(TOKEN_NOT_FOUND);
        }
        User user = new User(userEmail, userPassword,username, role,User.ProvidersList.SOOLZZAK,null);
        userRepository.save(user);

        return ResponseEntity.ok(ResponseDto.setSuccess("회원가입이 완료되었습니다.",null));
    }

    @Transactional
    public ResponseEntity login(UserLoginDto requestDto, HttpServletResponse response) {

        String userEmail = requestDto.getEmail();
        String userPassword = requestDto.getPassword();

        try {
            User user = userRepository.findUserByEmail(userEmail).orElseThrow(
                    () -> new ApiException(USER_NOT_FOUND)
            );

            if(!passwordEncoder.matches(userPassword, user.getPassword())){
                throw new ApiException(INVALID_PASSWORD);
            }

            TokenDto tokenDto = jwtUtil.createAllToken(requestDto.getEmail(), user.getRole());
            Optional<RefreshToken> refreshToken = refreshTokenRepository.findRefreshTokenByUserEmail(requestDto.getEmail());

            if (refreshToken.isPresent()) {
                RefreshToken savedRefreshToken = refreshToken.get();
                RefreshToken updateToken = savedRefreshToken.updateToken(tokenDto.getRefreshToken().substring(7));
                refreshTokenRepository.save(updateToken);
            } else {
                RefreshToken newToken = new RefreshToken(tokenDto.getRefreshToken().substring(7), userEmail);
                refreshTokenRepository.save(newToken);
            }
            setHeader(response, tokenDto, user.getEmail());

            ResponseDto responseDto = ResponseDto.setSuccess("로그인에 성공하였습니다.", null);
            return new ResponseEntity(responseDto, HttpStatus.OK);

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
    public ResponseEntity logout(String userEmail) {
        RefreshToken refreshToken = refreshTokenRepository.findRefreshTokenByUserEmail(userEmail)
                .orElseThrow(() -> new ApiException(TOKEN_NOT_FOUND)
                );
        refreshTokenRepository.delete(refreshToken);
        ResponseDto responseDto = ResponseDto.setSuccess("정상적으로 로그아웃하였습니다.", null);
        return new ResponseEntity(responseDto, HttpStatus.OK);
    }

    private boolean hasBadWord(String input) {
        for (String badWord : BadWords.koreaWord1) {
            if (input.contains(badWord)) {
                return true;
            }
        }
        return false;
    }
}

