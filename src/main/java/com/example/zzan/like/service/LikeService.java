package com.example.zzan.like.service;

import com.example.zzan.global.dto.ResponseDto;
import com.example.zzan.global.exception.ApiException;
import com.example.zzan.global.security.UserDetailsImpl;
import com.example.zzan.like.entity.Like;
import com.example.zzan.like.entity.LikeEnum;
import com.example.zzan.like.repository.LikeRepository;
import com.example.zzan.user.entity.User;
import com.example.zzan.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.example.zzan.global.exception.ExceptionEnum.NOT_ALLOWED_SELF_LIKE;
import static com.example.zzan.global.exception.ExceptionEnum.TARGET_USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;

    @Transactional
    public ResponseDto updateAlcohol(Long targetId, boolean like) {
        UserDetailsImpl currentUser = getCurrentUser();
        Long userId = currentUser.getUser().getId();
        Optional<User> targetOptional = userRepository.findById(targetId);

        if (targetOptional.isPresent()) {
            User targetUser = targetOptional.get();

            if (userId.equals(targetId)) {
                throw new ApiException(NOT_ALLOWED_SELF_LIKE);
            }

            User user = userRepository.getOne(userId);

            Like existingLike = likeRepository.findByUserAndTargetUserAndLikeEnum(user, targetUser, like ? LikeEnum.LIKE : LikeEnum.DISLIKE);

            if (existingLike == null) {
                targetUser.setAlcohol(targetUser.getAlcohol() + (like ? 1 : -1));
                userRepository.save(targetUser);
                Like likeEntity = new Like(user, targetUser, like ? LikeEnum.LIKE : LikeEnum.DISLIKE);
                likeRepository.save(likeEntity);
                return ResponseDto.setSuccess(like ? "도수를 올렸습니다." : "도수를 내렸습니다.");
            } else {
                targetUser.setAlcohol(targetUser.getAlcohol() - (like ? 1 : -1));
                userRepository.save(targetUser);
                likeRepository.delete(existingLike);
                return ResponseDto.setSuccess(like ? "도수 올리기를 취소하셨습니다." : "도수 내리기를 취소하셨습니다.");
            }
        } else {
            throw new ApiException(TARGET_USER_NOT_FOUND);
        }
    }

    private UserDetailsImpl getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UserDetailsImpl) authentication.getPrincipal();
    }
}