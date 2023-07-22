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

import static com.example.zzan.global.exception.ExceptionEnum.NOT_ALLOWED_SELF_BLOCK;
import static com.example.zzan.global.exception.ExceptionEnum.TARGET_USER_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class BlockListService {
    private final BlockListRepository blockListRepository;
    private final UserRepository userRepository;

    @Transactional
    public ResponseDto updateBlock(Long blockListedUserId, User user) {
        Optional<User> blockListedUserOptional = userRepository.findById(blockListedUserId);

        User blockListedUser = blockListedUserOptional.orElseThrow(() -> new ApiException(TARGET_USER_NOT_FOUND));

        if (user.getId().equals(blockListedUser.getId())) {
            throw new ApiException(NOT_ALLOWED_SELF_BLOCK);
        }

        Optional<BlockList> existingBlock = blockListRepository.findByBlockListedUserAndBlockListingUser(blockListedUser, user);

        if (existingBlock.isPresent()) {
            blockListRepository.delete(existingBlock.get());
            return ResponseDto.setSuccess("Successfully unblocked the user.");
        } else {
            BlockList blockList = new BlockList(blockListedUser, user);
            blockListRepository.save(blockList);
            return ResponseDto.setSuccess("Successfully blocked the user.");
        }
    }
}
