package com.example.zzan.mypage.service;


import com.example.zzan.blacklist.dto.BlacklistDto;
import com.example.zzan.blacklist.entity.Blacklist;
import com.example.zzan.blacklist.repository.BlacklistRepository;
import com.example.zzan.follow.dto.FollowResponseDto;
import com.example.zzan.follow.entity.Follow;
import com.example.zzan.follow.repository.FollowRepository;
import com.example.zzan.global.dto.ResponseDto;
import com.example.zzan.global.exception.ApiException;
import com.example.zzan.global.util.BadWords;
import com.example.zzan.mypage.dto.MyPageResponseDto;
import com.example.zzan.mypage.dto.MypageChangeDto;
import com.example.zzan.user.entity.User;
import com.example.zzan.user.repository.UserRepository;
import com.example.zzan.userHistory.dto.UserHistoryDto;
import com.example.zzan.userHistory.entity.UserHistory;
import com.example.zzan.userHistory.repository.UserHistoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
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
	private final UserHistoryRepository userHistoryRepository;
	private final FollowRepository followRepository;
	private final BlacklistRepository blacklistRepository;

	@Transactional
	public ResponseDto<MypageChangeDto> saveMyPage(MultipartFile userImage, String username, String email) throws IOException {
		String storedFileName = null;
		if(userImage != null && !userImage.isEmpty()) {
			storedFileName = s3Uploader.upload(userImage, "images");
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
				myPage.UserImgurl(storedFileName);
			}
			userRepository.save(myPage);
		} else {
			throw new ApiException(ROOM_NOT_FOUND);
		}

		// return new MyPageResponseDto(myPage);
		return ResponseDto.setSuccess("프로필이 저장되었습니다",new MypageChangeDto(myPage));

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
		Pageable topTwenty = PageRequest.of(0, 20);

		List<UserHistory> userHistories = userHistoryRepository.findTop20ByHostUserOrEnterUserOrderByCreatedAtDesc(user, topTwenty);
		List<Follow> follows = followRepository.findAllByFollowerUser(user);
		List<Blacklist> blacklists = blacklistRepository.findAllByBlackListingUser(user);

		List<UserHistoryDto> userHistoryDtos = new ArrayList<>();
		List<FollowResponseDto> followResponseDtos = new ArrayList<>();
		List<BlacklistDto> blacklistDtos = new ArrayList<>();
		User myPage = findUser(user.getEmail());
		String socialProvider= user.getProviders();

		for (UserHistory userHistory : userHistories) {

			String meetedUser = "";  // 변수를 블록 외부에서 선언하고 초기화
			String metUserImage = "";

			if(userHistory.getHostUser().getUsername().equals(user.getUsername())){
				meetedUser = userHistory.getGuestUser().getUsername();
				metUserImage=userHistory.getGuestUser().getUserImage();
			}else if(!userHistory.getHostUser().getUsername().equals(user.getUsername())){
				meetedUser = userHistory.getHostUser().getUsername();
				metUserImage=userHistory.getHostUser().getUserImage();
			}

			LocalDateTime createdAt = userHistory.getCreatedAt();

			UserHistoryDto userHistoryDto = new UserHistoryDto(meetedUser,createdAt,metUserImage);
			userHistoryDtos.add(userHistoryDto);
		}

		for (Follow follow : follows){
			String followingUser =follow.getFollowingUser().getUsername();
			FollowResponseDto followResponseDto=new FollowResponseDto(followingUser);
			followResponseDtos.add(followResponseDto);
		}

		for (Blacklist blacklist : blacklists){
			String blacklistedUser =blacklist.getBlackListedUser().getUsername();
			BlacklistDto blacklistDto=new BlacklistDto(blacklistedUser);
			blacklistDtos.add(blacklistDto);
		}


		return ResponseDto.setSuccess("기록이 조회되었습니다", new MyPageResponseDto(myPage, myPage.getAlcohol(),socialProvider,userHistoryDtos,followResponseDtos,blacklistDtos));
	}
}

