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

import java.util.Optional;

import static com.example.zzan.global.exception.ExceptionEnum.*;

@Service
@RequiredArgsConstructor
public class FollowService {

	private final FollowRepository followRepository;
	private final UserRepository userRepository;


	@Transactional
	public ResponseDto<FollowResponseDto> getFollow(Long followId, User user) {

		Optional<User> followingUser = userRepository.findById(followId);
		Optional<Follow> followList = followRepository.findByFollowingIdAndFollowerId(followingUser.get(), user);

		if (user.getId().equals(followId)) {
			throw new ApiException(NOT_ALLOWED_SELF_FOLLOW);
		}

		if(followingUser.isPresent() && !followList.isPresent()){
			Follow follow = new Follow(followingUser.get(), user);
			followRepository.save(follow);
			return ResponseDto.setSuccess("팔로잉하였습니다");

		} else if (followList.isPresent()) {
			throw new ApiException(ALREADY_FOLLOWING);
		} else
			throw new ApiException(USER_NOT_FOUND);
	}

	public ResponseDto<FollowResponseDto> deleteFollow(Long followId, User user) {
		Optional<User> followingUser = userRepository.findById(followId);
		Optional<Follow> followList = followRepository.findByFollowingIdAndFollowerId(followingUser.get(), user);

		if (user.getId().equals(followId)) {
			throw new ApiException(NOT_ALLOWED_SELF_FOLLOW);
		}

		if (followingUser.isPresent() && followList.isPresent()) {
			followRepository.delete(followList.get());
			return ResponseDto.setSuccess("팔로우를 취소 하였습니다");
		} else {
			throw new ApiException(TARGET_USER_NOT_FOUND);
		}
	}

}