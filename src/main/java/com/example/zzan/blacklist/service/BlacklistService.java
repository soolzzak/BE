package com.example.zzan.blacklist.service;

import static com.example.zzan.global.exception.ExceptionEnum.*;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.zzan.blacklist.entity.Blacklist;
import com.example.zzan.blacklist.repository.BlacklistRepository;
import com.example.zzan.global.dto.ResponseDto;
import com.example.zzan.global.exception.ApiException;
import com.example.zzan.user.entity.User;
import com.example.zzan.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
public class BlacklistService {

	private final BlacklistRepository blacklistRepository;
	private final UserRepository userRepository;
	public ResponseDto addBlacklist(Long blackListedUserId, User user) {

		Optional<User> blackListedUserOptional= userRepository.findById(blackListedUserId);


		if(!blackListedUserOptional.isPresent()) {
			throw new ApiException(TARGET_USER_NOT_FOUND);
		}

		User blackListedUser=blackListedUserOptional.get();

		if(user.getId().equals(blackListedUser.getId())){
			throw new ApiException(USER_CANNOT_REPORT_SELF);
		}

		Blacklist blacklist=new Blacklist(blackListedUserOptional.get(),user);
		blacklistRepository.save(blacklist);
		
		return ResponseDto.setSuccess("차단되었습니다");
	}

}
