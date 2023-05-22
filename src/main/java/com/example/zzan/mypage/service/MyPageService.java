package com.example.zzan.mypage.service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.zzan.mypage.dto.MyPageResponseDto;
import com.example.zzan.mypage.entity.MyPage;
import com.example.zzan.mypage.repository.MyPageRepository;

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

	private final MyPageRepository myPageRepository;
	private final S3Uploader s3Uploader;


	@Transactional
	public MyPageResponseDto saveMyPage(MultipartFile image, String nickname) throws IOException {
		String storedFileName = null;
		if(!image.isEmpty()) {
			storedFileName = s3Uploader.upload(image, "images");
		}
		MyPage myPage = new MyPage(nickname, storedFileName);
		MyPage savedMyPage = myPageRepository.save(myPage);
		return new MyPageResponseDto(myPage);
	}
}

