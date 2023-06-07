package com.example.zzan.blocklist.service;

import com.example.zzan.blocklist.entity.BlockList;
import com.example.zzan.blocklist.repository.BlockListRepository;
import com.example.zzan.global.dto.ResponseDto;
import com.example.zzan.global.exception.ApiException;
import com.example.zzan.user.entity.User;
import com.example.zzan.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.example.zzan.global.exception.ExceptionEnum.TARGET_USER_NOT_FOUND;
import static com.example.zzan.global.exception.ExceptionEnum.USER_CANNOT_BLOCK_SELF;

@RequiredArgsConstructor
@Service
public class BlockListService {

	private final BlockListRepository blockListRepository;
	private final UserRepository userRepository;

	@Transactional
	public ResponseDto updateBlock(Long blockListedUserId, User user) {
		Optional<User> blockListedUserOptional = userRepository.findById(blockListedUserId);

		if (!blockListedUserOptional.isPresent()) {
			throw new ApiException(TARGET_USER_NOT_FOUND);
		}

		User blockListedUser = blockListedUserOptional.get();

		if (user.getId().equals(blockListedUser.getId())) {
			throw new ApiException(USER_CANNOT_BLOCK_SELF);
		}

		Optional<BlockList> existingBlock = blockListRepository.findByBlockListedUserAndBlockListingUser(blockListedUser, user);

		if (existingBlock.isPresent()) {
			blockListRepository.delete(existingBlock.get());
			return ResponseDto.setSuccess("차단이 해제되었습니다.");
		} else {
			BlockList blockList = new BlockList(blockListedUser, user);
			blockListRepository.save(blockList);
			return ResponseDto.setSuccess("차단되었습니다.");
		}
	}
}
