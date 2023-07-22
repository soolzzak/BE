package com.example.zzan.mypage.dto;

import com.example.zzan.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MypageChangeDto {
    private String userImage;
    private String username;
    private String introduction;

    public MypageChangeDto(User myPage) {
        this.userImage = myPage.getUserImage();
        this.username = myPage.getUsername();
        this.introduction = myPage.getIntroduction();
    }
}