package com.example.zzan.mypage.service;

<<<<<<< Updated upstream
=======
import static com.example.zzan.global.exception.ExceptionEnum.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.zzan.global.util.BadWords;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

>>>>>>> Stashed changes
import com.example.zzan.global.dto.ResponseDto;
import com.example.zzan.global.exception.ApiException;
import com.example.zzan.global.util.BadWords;
import com.example.zzan.mypage.dto.MyPageResponseDto;
import com.example.zzan.mypage.dto.MypageChangeDto;
import com.example.zzan.roomreport.dto.UserReportDto;
import com.example.zzan.roomreport.entity.UserReport;
import com.example.zzan.roomreport.repository.UserReportRepository;
import com.example.zzan.user.entity.User;
import com.example.zzan.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.zzan.global.exception.ExceptionEnum.NOT_ALLOWED_USERNAME;
import static com.example.zzan.global.exception.ExceptionEnum.ROOM_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class MyPageService {

	private final UserRepository userRepository;
	private final S3Uploader s3Uploader;
	private final UserReportRepository userReportRepository;

	@Transactional
	public ResponseDto<MypageChangeDto> saveMyPage(MultipartFile image, String username, String email) throws IOException {
		String storedFileName = null;
		if(image != null && !image.isEmpty()) {
			storedFileName = s3Uploader.upload(image, "images");
		}
		User myPage = findUser(email);
		if(myPage != null) {
			if (username != null && !username.trim().isEmpty()) {
				if (hasBadWord(username)){
					throw new ApiException(NOT_ALLOWED_USERNAME);
				}
				myPage.username(username);
			}
			if (storedFileName != null) {
				myPage.UserImg(storedFileName);
			}
			userRepository.save(myPage);
		} else {
			throw new ApiException(ROOM_NOT_FOUND);
		}
<<<<<<< Updated upstream
		return ResponseDto.setSuccess("프로필이 저장되었습니다",new MyPageResponseDto(myPage));
=======
		// return new MyPageResponseDto(myPage);
		return ResponseDto.setSuccess("프로필이 저장되었습니다",new MypageChangeDto(myPage));
>>>>>>> Stashed changes
	}


	public User findUser(String email) {
		Optional<User> optionalUser = userRepository.findUserByEmail(email);
		return optionalUser.orElse(null);
	}

	private boolean hasBadWord(String input) {
		for (String badWord : BadWords.koreaWord1) {
			if (input.contains(badWord)) {
				return true;
			}
		}
		return false;
	}


	@Transactional
	public ResponseDto<MyPageResponseDto> getUserInfo(User user) {
		Pageable topThree = PageRequest.of(0, 3);
		List<UserReport> userReports = userReportRepository.findTop3ByHostUserOrEnterUserOrderByCreatedAtDesc(user, topThree);
		List<UserReportDto> userReportDtos = new ArrayList<>();
		User myPage = findUser(user.getEmail());

		for (UserReport userReport : userReports) {

			String meetedUser = "";  // 변수를 블록 외부에서 선언하고 초기화

			if(userReport.getHostUser().getUsername().equals(user.getUsername())){
				meetedUser = userReport.getEnterUser().getUsername();
			}else if(!userReport.getHostUser().getUsername().equals(user.getUsername())){
				meetedUser = userReport.getHostUser().getUsername();
			}

			LocalDateTime createdAt = userReport.getCreatedAt();

			UserReportDto userReportDto = new UserReportDto(meetedUser,createdAt);
			userReportDtos.add(userReportDto);
		}

		return ResponseDto.setSuccess("기록이 조회되었습니다", new MyPageResponseDto(myPage, myPage.getAlcohol(),userReportDtos));
	}


}

