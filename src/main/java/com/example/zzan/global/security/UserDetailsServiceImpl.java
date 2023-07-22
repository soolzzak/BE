package com.example.zzan.global.security;

import com.example.zzan.global.exception.ApiException;
import com.example.zzan.user.entity.User;
import com.example.zzan.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.example.zzan.global.exception.ExceptionEnum.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(userEmail)
                .orElseThrow(() -> new ApiException(USER_NOT_FOUND));

        return new UserDetailsImpl(user, user.getEmail());
    }
}