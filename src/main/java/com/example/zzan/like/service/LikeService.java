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
    public ResponseDto increaseAlcohol(Long targetId, boolean like) {
        UserDetailsImpl currentUser = getCurrentUser();
        Long userId = currentUser.getUser().getId();
        Optional<User> targetOptional = userRepository.findById(targetId);

        if (targetOptional.isEmpty()) {
            throw new ApiException(TARGET_USER_NOT_FOUND);
        }

        User targetUser = targetOptional.get();

        if (userId.equals(targetId)) {
            throw new ApiException(NOT_ALLOWED_SELF_LIKE);
        }

        User user = userRepository.getById(userId);

        Like existingLike = likeRepository.findByUserAndTargetUserAndLikeEnum(user, targetUser, LikeEnum.LIKE);

        if (existingLike == null) {
            targetUser.setAlcohol(targetUser.getAlcohol() + 1);
            userRepository.save(targetUser);
            Like likeEntity = new Like(user, targetUser, LikeEnum.LIKE);
            likeRepository.save(likeEntity);
            targetUser.setAlcoholUp(like);
            targetUser.setAlcoholDown(!like);
            return ResponseDto.setSuccess("Successfully increased the alcohol status.");
        } else {
            if (like && existingLike.getLikeEnum() == LikeEnum.LIKE) {
                targetUser.setAlcohol(targetUser.getAlcohol() - 1);
                userRepository.save(targetUser);
                likeRepository.delete(existingLike);
                targetUser.setAlcoholUp(false);
                targetUser.setAlcoholDown(false);
                return ResponseDto.setSuccess("Successfully cancelled the increase of the alcohol status.");
            } else {
                targetUser.setAlcohol(targetUser.getAlcohol() + 2);
                likeRepository.delete(existingLike);
                Like likeEntity = new Like(user, targetUser, LikeEnum.LIKE);
                likeRepository.save(likeEntity);
                targetUser.setAlcoholUp(true);
                targetUser.setAlcoholDown(false);
                return ResponseDto.setSuccess("Successfully cancelled the decrease and increased the alcohol status.");
            }
        }
    }

    @Transactional
    public ResponseDto decreaseAlcohol(Long targetId, boolean like) {
        UserDetailsImpl currentUser = getCurrentUser();
        Long userId = currentUser.getUser().getId();
        Optional<User> targetOptional = userRepository.findById(targetId);

        if (targetOptional.isEmpty()) {
            throw new ApiException(TARGET_USER_NOT_FOUND);
        }

        User targetUser = targetOptional.get();

        if (userId.equals(targetId)) {
            throw new ApiException(NOT_ALLOWED_SELF_LIKE);
        }

        User user = userRepository.getById(userId);

        Like existingLike = likeRepository.findByUserAndTargetUserAndLikeEnum(user, targetUser, LikeEnum.DISLIKE);

        if (existingLike == null) {
            targetUser.setAlcohol(targetUser.getAlcohol() - 1);
            userRepository.save(targetUser);
            Like likeEntity = new Like(user, targetUser, LikeEnum.DISLIKE);
            likeRepository.save(likeEntity);
            targetUser.setAlcoholUp(like);
            targetUser.setAlcoholDown(!like);
            return ResponseDto.setSuccess("Successfully decreased the alcohol status.");
        } else {
            if (!like && existingLike.getLikeEnum() == LikeEnum.DISLIKE) {
                targetUser.setAlcohol(targetUser.getAlcohol() + 1);
                userRepository.save(targetUser);
                likeRepository.delete(existingLike);
                targetUser.setAlcoholUp(false);
                targetUser.setAlcoholDown(false);
                return ResponseDto.setSuccess("Successfully cancelled the decrease of the alcohol status.");
            } else {
                targetUser.setAlcohol(targetUser.getAlcohol() - 2);
                likeRepository.delete(existingLike);
                Like likeEntity = new Like(user, targetUser, LikeEnum.DISLIKE);
                likeRepository.save(likeEntity);
                targetUser.setAlcoholUp(false);
                targetUser.setAlcoholDown(true);
                return ResponseDto.setSuccess("Successfully cancelled the increase and decreased the alcohol status.");
            }
        }
    }

    private UserDetailsImpl getCurrentUser() {
        return (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}