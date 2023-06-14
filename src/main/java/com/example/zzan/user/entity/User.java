package com.example.zzan.user.entity;

import com.example.zzan.room.entity.Room;
import com.example.zzan.user.dto.KakaoInfoDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity(name = "TB_USER")
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long id;

    @Column
    private String kakaoId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private Date birthday;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Gender gender;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRole role;

    @Column(nullable = true)
    private String userImage;

    @Column(nullable = false)
    private int reportPoints = 0;

    @Column(nullable = false)
    @Min(value = 0, message = "도수는 0도 미만으로 내릴 수 없습니다.")
    @Max(value = 100, message = "도수는 100도를 초과할 수 없습니다.")
    private int alcohol;

    @Column(name = "LIKES")
    private boolean alcoholUp = false;

    @Column(name = "DISLIKES")
    private boolean alcoholDown = false;

    @ManyToOne
    @JoinColumn(name = "ROOM_ID")
    private Room room;

    @Column(name = "ROOM_TITLE")
    private String roomTitle;

    @PrePersist
    public void setDefaultValues() {
        if (alcohol == 0) {
            alcohol = 16;
        }
    }

    public User(String email, String password, String username, UserRole role, String userImage, Gender gender) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.role = role;
        this.userImage = userImage;
        this.gender = gender;
    }

    public User(KakaoInfoDto kakaoInfoDto) {
        this.username = kakaoInfoDto.getUsername();
        this.kakaoId = kakaoInfoDto.getKakaoId().toString();
    }

    // public User(KakaoInfoDto kakaoInfoDto, String email, String password, String username, UserRole role, String userImage, Gender gender, Date birthday) {
    //     this.kakaoId =kakaoInfoDto.getKakaoId().toString();
    //     this.email = email;
    //     this.password = password;
    //     this.username = username;
    //     this.role = role;
    //     this.userImage = userImage;
    //     this.gender = gender;
    //     this.birthday = birthday;
    //     this.alcohol = 16;
    //     this.alcoholUp = false;
    //     this.alcoholDown = false;
    // }

    public User(KakaoInfoDto kakaoInfoDto,String password,UserRole role,String userImage,Date birthday) {
        this.kakaoId =kakaoInfoDto.getKakaoId().toString();
        this.email = kakaoInfoDto.getEmail();
        this.password = password;
        this.username = kakaoInfoDto.getUsername();
        this.role = role;
        this.userImage = userImage;
        this.gender = kakaoInfoDto.getGender();
        this.birthday = birthday;
        this.alcohol = 16;
        this.alcoholUp = false;
        this.alcoholDown = false;
    }





    public User(String username, String userImage) {
        this.username = username;
        this.userImage = userImage;
    }

    public void UserImgurl(String userImage) {
        this.userImage = userImage;
    }

    public void username(String username) {
        this.username = username;
    }

    public void addReportPoints(int points) {
        this.reportPoints += points;
    }
}
