package com.example.zzan.mypage.service;

import static com.example.zzan.global.exception.ExceptionEnum.*;
import java.io.IOException;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.example.zzan.global.exception.ApiException;
import com.example.zzan.mypage.dto.MyPageResponseDto;
import com.example.zzan.user.entity.User;
import com.example.zzan.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyPageService {

	private final UserRepository userRepository;
	private final S3Uploader s3Uploader;
	
	@Transactional
	public MyPageResponseDto saveMyPage(MultipartFile image, String username, String email) throws IOException {
		String storedFileName = null;
		if(image != null && !image.isEmpty()) {
			storedFileName = s3Uploader.upload(image, "images");
		}
		User myPage = findUser(email);
		if(myPage != null) {
			if (username != null && !username.trim().isEmpty()) {
				myPage.username(username);
			}
			if (storedFileName != null) {
				myPage.UserImg(storedFileName);
			}
			userRepository.save(myPage);
		} else {
			throw new ApiException(ROOM_NOT_FOUND);
		}
		return new MyPageResponseDto(myPage);
	}

	public User findUser(String email) {
		Optional<User> optionalUser = userRepository.findUserByEmail(email);
		return optionalUser.orElse(null);
	}
}

