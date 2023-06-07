package com.example.zzan.blocklist.service;

import static com.example.zzan.global.exception.ExceptionEnum.*;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.zzan.blocklist.entity.BlockList;
import com.example.zzan.blocklist.repository.BlockListRepository;
import com.example.zzan.global.dto.ResponseDto;
import com.example.zzan.global.exception.ApiException;
import com.example.zzan.user.entity.User;
import com.example.zzan.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class BlockListService {

	private final BlockListRepository blockListRepository;
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

		BlockList blockList =new BlockList(blackListedUserOptional.get(),user);
		blockListRepository.save(blockList);
		
		return ResponseDto.setSuccess("차단되었습니다");
	}
}
