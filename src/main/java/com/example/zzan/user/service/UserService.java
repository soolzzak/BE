package com.example.zzan.user.service;

import com.example.zzan.global.dto.ResponseDto;
import com.example.zzan.global.exception.ApiException;
import com.example.zzan.global.exception.BadWords;
import com.example.zzan.global.jwt.JwtUtil;
import com.example.zzan.global.security.dto.TokenDto;
import com.example.zzan.global.security.entity.RefreshToken;
import com.example.zzan.global.security.repository.RefreshTokenRepository;
import com.example.zzan.global.util.S3Uploader;
import com.example.zzan.user.dto.PasswordRequestDto;
import com.example.zzan.user.dto.UserLoginDto;
import com.example.zzan.user.dto.UserRequestDto;
import com.example.zzan.user.entity.Gender;
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

import java.util.Date;
import java.util.Optional;

import static com.example.zzan.global.exception.ExceptionEnum.*;
import static com.example.zzan.global.jwt.JwtUtil.ACCESS_KEY;
import static com.example.zzan.global.jwt.JwtUtil.REFRESH_KEY;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final S3Uploader s3Uploader;
    private static final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";

    @Transactional
    public ResponseEntity<?> signup(UserRequestDto requestDto) {

        String username = requestDto.getUsername();

        if (hasBadWord(username)) {
            throw new ApiException(NOT_ALLOWED_USERNAME);
        }

        String userEmail = requestDto.getEmail();
        Optional<User> found = userRepository.findUserByEmail(userEmail);
        if (found.isPresent()) {
            throw new ApiException(EMAIL_DUPLICATION);
        }
        if (userEmail == null || !userEmail.matches("^[A-Za-z0-9_\\.\\-]+@[A-Za-z0-9\\-]+\\.[A-Za-z0-9\\-]+$")) {
            throw new ApiException(INVALID_EMAIL);
        }

        String password = requestDto.getPassword();
        if (password == null || password.length() < 8 || password.length() > 15 || !password.matches("^[a-zA-Z\\p{Punct}0-9]*$")) {
            throw new ApiException(INVALID_PASSWORD);
        }
        String userPassword = passwordEncoder.encode(requestDto.getPassword());

        Date birthday = requestDto.getBirthday();
        if (birthday == null) {
            throw new ApiException(INVALID_BIRTHDAY);
        }

        Gender gender = requestDto.getGender();
        if (!"Male".equalsIgnoreCase(String.valueOf(gender)) && !"Female".equalsIgnoreCase(String.valueOf(gender))) {
            throw new ApiException(INVALID_GENDER);
        }

        UserRole role = requestDto.isAdmin() ? UserRole.ADMIN : UserRole.USER;
        if (role == UserRole.ADMIN && !ADMIN_TOKEN.equals(requestDto.getAdminKey())) {
            throw new ApiException(ACCESS_TOKEN_NOT_FOUND);
        }

        User user = new User(userEmail, userPassword, username, role, s3Uploader.getRandomImage("profile"), gender);
        user.setBirthday(birthday);
        userRepository.save(user);
        return ResponseEntity.ok(ResponseDto.setSuccess("Signup registration has been completed.", null));
    }

    @Transactional
    public ResponseEntity<?> changePassword(PasswordRequestDto passwordRequestDto) {
        User user = userRepository.findByEmail(passwordRequestDto.getEmail());
        if (user == null) {
            throw new ApiException(EMAIL_NOT_FOUND);
        }

        String password = passwordRequestDto.getPassword();
        if (password == null || password.length() < 8 || password.length() > 15 || !password.matches("^[a-zA-Z\\p{Punct}0-9]*$")) {
            throw new ApiException(INVALID_PASSWORD);
        }

        String userPassword = passwordEncoder.encode(passwordRequestDto.getPassword());
        user.setPassword(userPassword);
        userRepository.save(user);

        return ResponseEntity.ok(ResponseDto.setSuccess("Password change completed.", null));
    }

    @Transactional
    public ResponseEntity<?> login(UserLoginDto requestDto, HttpServletResponse response) {

        String userEmail = requestDto.getEmail();
        String userPassword = requestDto.getPassword();

        try {
            User user = userRepository.findUserByEmail(userEmail).orElseThrow(
                    () -> new ApiException(EMAIL_NOT_FOUND)
            );

            if (!passwordEncoder.matches(userPassword, user.getPassword())) {
                throw new ApiException(PASSWORD_NOT_MATCH);
            }

            TokenDto tokenDto = jwtUtil.createAllToken(user, user.getRole());
            Optional<RefreshToken> refreshToken = refreshTokenRepository.findRefreshTokenByUserEmail(requestDto.getEmail());

            if (refreshToken.isPresent()) {
                RefreshToken savedRefreshToken = refreshToken.get();
                RefreshToken updateToken = savedRefreshToken.updateToken(tokenDto.getRefreshToken().substring(7));
                refreshTokenRepository.save(updateToken);
            } else {
                RefreshToken newToken = new RefreshToken(tokenDto.getRefreshToken().substring(7), userEmail, user.getId());
                refreshTokenRepository.save(newToken);
            }
            setHeader(response, tokenDto, user.getEmail());

            ResponseDto responseDto = ResponseDto.setSuccess("Login successful.", null);
            return new ResponseEntity<>(responseDto, HttpStatus.OK);

        } catch (IllegalArgumentException e) {

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    private void setHeader(HttpServletResponse response, TokenDto tokenDto, String userEmail) {
        response.addHeader(ACCESS_KEY, tokenDto.getAccessToken());
        response.addHeader(REFRESH_KEY, tokenDto.getRefreshToken());
        response.addHeader("USER-EMAIL", userEmail);
    }

    @Transactional
    public ResponseEntity<?> logout(User user) {
        RefreshToken refreshToken = refreshTokenRepository.findRefreshTokenByUserId(user.getId())
                .orElseThrow(() -> new ApiException(ACCESS_TOKEN_NOT_FOUND)
                );

        refreshTokenRepository.delete(refreshToken);
        ResponseDto responseDto = ResponseDto.setSuccess("Successfully logged out.", null);
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

