package com.example.zzan.mypage.service;

import static com.example.zzan.global.exception.ExceptionEnum.*;

import java.io.IOException;
import java.util.Optional;

<<<<<<< HEAD
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.zzan.global.exception.ApiException;
import com.example.zzan.mypage.dto.MyPageResponseDto;
import com.example.zzan.user.entity.User;
import com.example.zzan.user.repository.UserRepository;
=======
import com.example.zzan.mypage.dto.MyPageResponseDto;
import com.example.zzan.mypage.entity.MyPage;
import com.example.zzan.mypage.repository.MyPageRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
>>>>>>> 9ab9d9e6494b51466b7b2ad6ffc31896e74f204b

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyPageService {

	// // 의존성 주입
	// private final MyPageRepository myPageRepository;
	//
	// @Autowired
	// private S3Uploader s3Uploader;

	private final UserRepository userRepository;
	private final S3Uploader s3Uploader;


	//s3Uploader.upload(image, "images") 메소드가 IOException을 발생시킬 수 있기 때문에 throws IOException을 메소드 선언에 추가
	@Transactional
	public MyPageResponseDto saveMyPage(MultipartFile image, String nickname,String email) throws IOException {
		String storedFileName = null;
		if(image != null && !image.isEmpty()) {
			storedFileName = s3Uploader.upload(image, "images");
		}

		User myPage = findUser(email);

		if(myPage != null) {
			if (nickname != null && !nickname.trim().isEmpty()) {
				myPage.Usernickname(nickname);//trim()양끝 공백을 제거
			}
			if (storedFileName != null) {
				myPage.Userimg(storedFileName);
			}
			userRepository.save(myPage);
		} else {
			// handle not found user
			throw new ApiException(ROOM_NOT_FOUND);
		}

		// MyPage savedMyPage = myPageRepository.save(myPage);
		return new MyPageResponseDto(myPage);
	}


	public User findUser(String email) {
		Optional<User> optionalUser = userRepository.findUserByEmail(email);
		return optionalUser.orElse(null);
	}


}

