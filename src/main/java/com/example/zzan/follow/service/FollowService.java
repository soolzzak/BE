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
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.example.zzan.global.exception.ExceptionEnum.*;

@Service
@RequiredArgsConstructor
public class FollowService {

	private final FollowRepository followRepository;
	private final UserRepository userRepository;
	private Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

	@Transactional
	public ResponseDto<FollowResponseDto> updateFollow (Long followId, User user) {
		Optional<User> followingUser = userRepository.findById(followId);

		if (user.getId().equals(followId)) {
			throw new ApiException(NOT_ALLOWED_SELF_FOLLOW);
		}

		if (followingUser.isPresent()) {
			Optional<Follow> followList = followRepository.findByFollowingUserAndFollowerUser(followingUser.get(), user);

			if (followList.isPresent()) {
				followRepository.delete(followList.get());
				return ResponseDto.setSuccess("팔로우를 취소 하였습니다");
			} else {
				Follow follow = new Follow(followingUser.get(), user);
				followRepository.save(follow);
				return ResponseDto.setSuccess("팔로잉하였습니다");
			}
		} else {
			throw new ApiException(USER_NOT_FOUND);
		}
	}

	public List<User> getFollowers(Long userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new ApiException(USER_NOT_FOUND));
		return followRepository.findByFollowingUser(user)
				.stream()
				.map(Follow::getFollowerUser)
				.collect(Collectors.toList());
	}
}