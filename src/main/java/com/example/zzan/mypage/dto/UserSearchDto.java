package com.example.zzan.mypage.dto;

import com.example.zzan.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserSearchDto {
    private Long userId;
    private String userImage;
    private String username;
    private String introduction;
    private int alcohol;
    private boolean alcoholUp;
    private boolean alcoholDown;
    private boolean followedByCurrentUser;
    private boolean blockedByCurrentUser;

    public UserSearchDto(User myPage, boolean followedByCurrentUser, boolean blockedByCurrentUser) {
        this.userId = myPage.getId();
        this.userImage = myPage.getUserImage();
        this.username = myPage.getUsername();
        this.introduction = myPage.getIntroduction();
        this.alcohol = myPage.getAlcohol();
        this.alcoholUp = myPage.isAlcoholUp();
        this.alcoholDown = myPage.isAlcoholDown();
        this.followedByCurrentUser = followedByCurrentUser;
        this.blockedByCurrentUser = blockedByCurrentUser;
    }
}

