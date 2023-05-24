package com.example.zzan.follow.service;

import static com.example.zzan.global.exception.ExceptionEnum.*;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.zzan.follow.dto.FollowResponseDto;
import com.example.zzan.follow.dto.FollowRuquestDto;
import com.example.zzan.follow.entity.Follow;
import com.example.zzan.follow.repository.FollowRepository;
import com.example.zzan.global.dto.ResponseDto;
import com.example.zzan.global.exception.ApiException;
import com.example.zzan.room.dto.RoomResponseDto;
import com.example.zzan.user.entity.User;
import com.example.zzan.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FollowService {

	private final FollowRepository followRepository;
	private final UserRepository userRepository;

	@Transactional
	public ResponseDto<FollowResponseDto> getFollow(FollowRuquestDto followRuquestDto, User user) {

		Optional<User> followllingUser = userRepository.findUserByEmail(followRuquestDto.getFollowingUserEmail());
		Optional<Follow>followList=followRepository.findByFollowingUserEmailAndUserEmail(followRuquestDto.getFollowingUserEmail(),user.getEmail());

		if(followllingUser.isPresent() && !followList.isPresent()){
			Follow follow=followRepository.save(new Follow(followRuquestDto,user));
			return ResponseDto.setSuccess("팔로잉하였습니다");

		} else if (followList.isPresent()) {
			throw new ApiException(USERS_DUPLICATION);
		} else
			throw new ApiException(USER_NOT_FOUND);
	}

}