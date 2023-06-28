package com.example.zzan.user.service;

import com.example.zzan.global.dto.ResponseDto;
import com.example.zzan.global.exception.ApiException;
import com.example.zzan.global.exception.BadWords;
import com.example.zzan.global.jwt.JwtUtil;
import com.example.zzan.global.security.dto.TokenDto;
import com.example.zzan.global.security.entity.RefreshToken;
import com.example.zzan.global.security.repository.RefreshTokenRepository;
import com.example.zzan.global.util.S3Uploader;
import com.example.zzan.redis.Service.RedisTokenService;
import com.example.zzan.room.dto.RoomResponseDto;
import com.example.zzan.user.dto.DeleteAccountRequestDto;
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
import jakarta.servlet.http.Cookie;

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
    private final RedisTokenService redisTokenService;
    private static final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";
 
    @Transactional
    public ResponseEntity<?> signup(UserRequestDto requestDto) {
        validateUsername(requestDto.getUsername());
        validateEmail(requestDto.getEmail());
        validatePassword(requestDto.getPassword());
        validateBirthday(requestDto.getBirthday());
        validateGender(requestDto.getGender());
        validateAdminKey(requestDto.isAdmin(), requestDto.getAdminKey());

        String userPassword = passwordEncoder.encode(requestDto.getPassword());

        Optional<User> foundUser = userRepository.findUserByEmail(requestDto.getEmail());
        User user;
        if (foundUser.isPresent() && foundUser.get().isDeleteAccount()) {
            user = foundUser.get();
            user.setUsername(requestDto.getUsername());
            user.setRole(requestDto.isAdmin() ? UserRole.ADMIN : UserRole.USER);
            user.setUserImage(s3Uploader.getRandomImage("profile"));
            user.setGender(requestDto.getGender());
            user.setBirthday(requestDto.getBirthday());
            user.setPassword(userPassword);
            user.setDeleteAccount(false);
        } else {
            user = new User(requestDto.getEmail(), userPassword, requestDto.getUsername(),
                requestDto.isAdmin() ? UserRole.ADMIN : UserRole.USER, s3Uploader.getRandomImage("profile"), requestDto.getGender());
            user.setBirthday(requestDto.getBirthday());
        }

        userRepository.save(user);
        return ResponseEntity.ok(ResponseDto.setSuccess("Signup registration has been completed.", null));
    }

    @Transactional
    public ResponseDto checkUsername(String username) {
        validateUsername(username);
        return ResponseDto.setSuccess("This nickname is available");
    }

    @Transactional
    public ResponseEntity<?> changePassword(PasswordRequestDto passwordRequestDto) {
        User user = userRepository.findByEmail(passwordRequestDto.getEmail());
        if (user == null) {
            throw new ApiException(EMAIL_NOT_FOUND);
        }

        validatePassword(passwordRequestDto.getPassword());

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

            if (user.isDeleteAccount()) {
                throw new ApiException(USER_HAS_LEFT);
            }

            if (!passwordEncoder.matches(userPassword, user.getPassword())) {
                throw new ApiException(PASSWORD_NOT_MATCH);
            }

            TokenDto tokenDto = jwtUtil.createAllToken(user, user.getRole());
            Optional<RefreshToken> refreshToken = refreshTokenRepository.findRefreshTokenByUserEmail(requestDto.getEmail());

            if (refreshToken.isPresent()) {
                RefreshToken savedRefreshToken = refreshToken.get();
                RefreshToken updateToken = savedRefreshToken.updateToken(tokenDto.getRefreshToken().substring(7));
                refreshTokenRepository.save(updateToken);
                redisTokenService.storeRefreshToken(user.getEmail(),updateToken.getRefreshToken());
            } else {
                RefreshToken newToken = new RefreshToken(tokenDto.getRefreshToken().substring(7), userEmail, user.getId());
                refreshTokenRepository.save(newToken);
                redisTokenService.storeRefreshToken(user.getEmail(),newToken.getRefreshToken());
            }
            setHeader(response, tokenDto, user.getEmail());

            ResponseDto responseDto = ResponseDto.setSuccess("Login successful.", null);
            return new ResponseEntity<>(responseDto, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // private void setHeader(HttpServletResponse response, TokenDto tokenDto, String userEmail) {
    //     response.addHeader(ACCESS_KEY, tokenDto.getAccessToken());
    //     response.addHeader(REFRESH_KEY, tokenDto.getRefreshToken());
    //     response.addHeader("USER-EMAIL", userEmail);
    // }


    private void setHeader(HttpServletResponse response, TokenDto tokenDto, String userEmail) {

        Cookie accessTokenCookie = new Cookie(ACCESS_KEY, tokenDto.getAccessToken());
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setPath("/");
        response.addCookie(accessTokenCookie);

        Cookie refreshTokenCookie = new Cookie(REFRESH_KEY, tokenDto.getRefreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        response.addCookie(refreshTokenCookie);

        response.addHeader("USER-EMAIL", userEmail);
    }



    @Transactional
    public ResponseEntity<?> logout(User user) {
        RefreshToken refreshToken = refreshTokenRepository.findRefreshTokenByUserId(user.getId())
                .orElseThrow(() -> new ApiException(ACCESS_TOKEN_NOT_FOUND));

        refreshTokenRepository.delete(refreshToken);
        ResponseDto responseDto = ResponseDto.setSuccess("Successfully logged out.", null);
        return new ResponseEntity(responseDto, HttpStatus.OK);
    }

    private void validateUsername(String username) {
        if (hasBadWord(username)) {
            throw new ApiException(NOT_ALLOWED_USERNAME);
        }
        if (userRepository.findByUsername(username).isPresent()&&!userRepository.findByUsername(username).get().isDeleteAccount() ) {
            throw new ApiException(USER_DUPLICATION);
        }
    }

    private void validateEmail(String email) {
        Optional<User> found = userRepository.findUserByEmail(email);
        if (found.isPresent()&& !found.get().isDeleteAccount()) {
            throw new ApiException(EMAIL_DUPLICATION);
        }
        if (email == null || !email.matches("^[A-Za-z0-9_\\.\\-]+@[A-Za-z0-9\\-]+\\.[A-Za-z0-9\\-]+$")) {
            throw new ApiException(INVALID_EMAIL);
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 8 || password.length() > 15 || !password.matches("^[a-zA-Z\\p{Punct}0-9]*$")) {
            throw new ApiException(INVALID_PASSWORD);
        }
    }

    private void validateBirthday(Date birthday) {
        if (birthday == null) {
            throw new ApiException(INVALID_BIRTHDAY);
        }
    }

    private void validateGender(Gender gender) {
        if (!"Male".equalsIgnoreCase(String.valueOf(gender)) && !"Female".equalsIgnoreCase(String.valueOf(gender))) {
            throw new ApiException(INVALID_GENDER);
        }
    }

    private void validateAdminKey(boolean isAdmin, String adminKey) {
        if (isAdmin && !ADMIN_TOKEN.equals(adminKey)) {
            throw new ApiException(ACCESS_TOKEN_NOT_FOUND);
        }
    }

    private boolean hasBadWord(String input) {
        return BadWords.koreaWord.stream().anyMatch(input::contains);
    }

    public ResponseDto deleteAccount(DeleteAccountRequestDto deleteAccountRequestDto,User user) {

        if(!passwordEncoder.matches(deleteAccountRequestDto.getPassword(), user.getPassword())){
            throw new ApiException(INVALID_ADMIN_INPUT);
        }else {
            user.setDeleteAccount(true);
            userRepository.save(user);
        }

        return ResponseDto.setSuccess("Your account has been successfully deleted");
    }

}