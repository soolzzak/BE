package com.example.zzan.follow.service;

import com.example.zzan.follow.dto.FollowResponseDto;
import com.example.zzan.follow.entity.Follow;
import com.example.zzan.follow.repository.FollowRepository;
import com.example.zzan.global.dto.ResponseDto;
import com.example.zzan.global.exception.ApiException;
import com.example.zzan.user.entity.User;
import com.example.zzan.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.zzan.global.exception.ExceptionEnum.NOT_ALLOWED_SELF_FOLLOW;
import static com.example.zzan.global.exception.ExceptionEnum.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Transactional
    public ResponseDto<FollowResponseDto> updateFollow(Long followId, User user) {
        if (user.getId().equals(followId)) {
            throw new ApiException(NOT_ALLOWED_SELF_FOLLOW);
        }

        User followingUser = userRepository.findById(followId)
                .orElseThrow(() -> new ApiException(USER_NOT_FOUND));

        Optional<Follow> followList = followRepository.findByFollowingUserAndFollowerUser(followingUser, user);

        if (followList.isPresent()) {
            followRepository.delete(followList.get());
            return ResponseDto.setSuccess("Successfully unfollowed the user.");
        } else {
            Follow follow = new Follow(followingUser, user);
            followRepository.save(follow);
            return ResponseDto.setSuccess("Successfully followed the user.");
        }
    }

    public List<User> getFollowers(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ApiException(USER_NOT_FOUND));
        return followRepository.findByFollowingUser(user).stream().map(Follow::getFollowerUser).collect(Collectors.toList());
    }
}