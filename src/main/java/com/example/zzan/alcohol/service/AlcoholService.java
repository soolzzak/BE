package com.example.zzan.alcohol.service;

import com.example.zzan.alcohol.entity.Alcohol;
import com.example.zzan.alcohol.repository.AlcoholRepository;
import com.example.zzan.global.dto.ResponseDto;
import com.example.zzan.global.exception.ApiException;
import com.example.zzan.user.entity.User;
import com.example.zzan.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.zzan.global.exception.ExceptionEnum.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class AlcoholService {
    private final UserRepository userRepository;
    private final AlcoholRepository alcoholRepository;

    @Transactional
    public ResponseDto<?> updateAlcohol(String email, User user) {
        User otherUser = userRepository.findUserByEmail(email).orElseThrow(
                () -> new ApiException(USER_NOT_FOUND)
        );

        Alcohol alcohol = alcoholRepository.findByUser(otherUser).orElse(new Alcohol(otherUser));

        boolean likeOrDislike = updateAlcoholLike(alcohol, user);

        alcoholRepository.save(alcohol);

        String message = likeOrDislike ? "도수가 올랐습니다." : "도수가 내렸습니다.";
        return ResponseDto.setSuccess(message, null);
    }

    private boolean updateAlcoholLike(Alcohol alcohol, User user) {
        boolean likeOrDislike;
        if (alcohol.getUserId().equals(user.getId())) {
            likeOrDislike = true;
            alcohol.getUser().updateAlcohol(true);
        } else {
            likeOrDislike = false;
            alcohol.getUser().updateAlcohol(false);
        }
        return likeOrDislike;
    }
}
