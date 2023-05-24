package com.example.zzan.like.service;

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

import static com.example.zzan.global.exception.ExceptionEnum.*;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;

    @Transactional
    public void updateAlcohol(Long targetId, boolean like) {
        UserDetailsImpl currentUser = getCurrentUser();
        Long userId = currentUser.getUser().getId();
        Optional<User> targetOptional = userRepository.findById(targetId);

        if (targetOptional.isPresent()) {
            User targetUser = targetOptional.get();

            if (userId.equals(targetId)) {
                throw new ApiException(NOT_ALLOWED_SELFLIKE);
            }

            targetUser.setAlcohol(targetUser.getAlcohol() + (like ? 1 : -1));
            userRepository.save(targetUser);

            User user = userRepository.getOne(userId);

            if (!likeRepository.existsByUserAndTargetUserAndLikeEnum(user, targetUser, like ? LikeEnum.LIKE : LikeEnum.DISLIKE)) {
                Like likeEntity = new Like(user, targetUser, like ? LikeEnum.LIKE : LikeEnum.DISLIKE);
                likeRepository.save(likeEntity);
            } else {
                Like likeEntity = new Like(user, targetUser, like ? LikeEnum.LIKE : LikeEnum.DISLIKE);
                likeRepository.delete(likeEntity);
            }
        } else {
            throw new ApiException(TARGETUSER_NOT_FOUND);
        }
    }

    private UserDetailsImpl getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UserDetailsImpl) authentication.getPrincipal();
    }
}
