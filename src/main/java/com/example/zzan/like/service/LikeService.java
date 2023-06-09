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
import static com.example.zzan.global.exception.ExceptionEnum.*;

import java.util.Optional;

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

        if (targetOptional.isPresent()) {
            User targetUser = targetOptional.get();

            if (userId.equals(targetId)) {
                throw new ApiException(NOT_ALLOWED_SELF_LIKE);
            }

            User user = userRepository.getOne(userId);

            Like existingLike = likeRepository.findByUserAndTargetUserAndLikeEnum(user, targetUser, LikeEnum.LIKE);

            if (existingLike == null) {
                targetUser.setAlcohol(targetUser.getAlcohol() + 1);
                userRepository.save(targetUser);
                Like likeEntity = new Like(user, targetUser, LikeEnum.LIKE);
                likeRepository.save(likeEntity);
                targetUser.setAlcoholUp(like);
                targetUser.setAlcoholDown(!like);
                return ResponseDto.setSuccess("도수를 올렸습니다.");
            } else {
                if (like && existingLike.getLikeEnum() == LikeEnum.LIKE) {
                    targetUser.setAlcohol(targetUser.getAlcohol() - 1);
                    userRepository.save(targetUser);
                    likeRepository.delete(existingLike);
                    targetUser.setAlcoholUp(false);
                    targetUser.setAlcoholDown(false);
                    return ResponseDto.setSuccess("도수 올리기를 취소하였습니다.");
                } else {
                    targetUser.setAlcohol(targetUser.getAlcohol() + 2);
                    likeRepository.delete(existingLike);
                    Like likeEntity = new Like(user, targetUser, LikeEnum.LIKE);
                    likeRepository.save(likeEntity);
                    targetUser.setAlcoholUp(true);
                    targetUser.setAlcoholDown(false);
                    return ResponseDto.setSuccess("도수 내리기 취소 후 도수 올리기 적용");
                }
            }
        } else {
            throw new ApiException(TARGET_USER_NOT_FOUND);
        }
    }

    @Transactional
    public ResponseDto decreaseAlcohol(Long targetId, boolean like) {
        UserDetailsImpl currentUser = getCurrentUser();
        Long userId = currentUser.getUser().getId();
        Optional<User> targetOptional = userRepository.findById(targetId);

        if (targetOptional.isPresent()) {
            User targetUser = targetOptional.get();

            if (userId.equals(targetId)) {
                throw new ApiException(NOT_ALLOWED_SELF_LIKE);
            }

            User user = userRepository.getOne(userId);

            Like existingLike = likeRepository.findByUserAndTargetUserAndLikeEnum(user, targetUser, LikeEnum.DISLIKE);

            if (existingLike == null) {
                targetUser.setAlcohol(targetUser.getAlcohol() - 1);
                userRepository.save(targetUser);
                Like likeEntity = new Like(user, targetUser, LikeEnum.DISLIKE);
                likeRepository.save(likeEntity);
                targetUser.setAlcoholUp(like);
                targetUser.setAlcoholDown(!like);
                return ResponseDto.setSuccess("도수를 내렸습니다.");
            } else {
                if (!like && existingLike.getLikeEnum() == LikeEnum.DISLIKE) {
                    targetUser.setAlcohol(targetUser.getAlcohol() + 1);
                    userRepository.save(targetUser);
                    likeRepository.delete(existingLike);
                    targetUser.setAlcoholUp(false);
                    targetUser.setAlcoholDown(false);
                    return ResponseDto.setSuccess("도수 내리기를 취소하였습니다.");
                } else {
                    targetUser.setAlcohol(targetUser.getAlcohol() - 2);
                    likeRepository.delete(existingLike);
                    Like likeEntity = new Like(user, targetUser, LikeEnum.DISLIKE);
                    likeRepository.save(likeEntity);
                    targetUser.setAlcoholUp(false);
                    targetUser.setAlcoholDown(true);
                    return ResponseDto.setSuccess("도수 올리기 취소 후 도수 내리기 적용");
                }
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