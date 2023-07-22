package com.example.zzan.mypage.service;

import com.example.zzan.blocklist.dto.BlockListDto;
import com.example.zzan.blocklist.entity.BlockList;
import com.example.zzan.blocklist.repository.BlockListRepository;
import com.example.zzan.follow.dto.FollowResponseDto;
import com.example.zzan.follow.entity.Follow;
import com.example.zzan.follow.repository.FollowRepository;
import com.example.zzan.global.dto.ResponseDto;
import com.example.zzan.global.exception.ApiException;
import com.example.zzan.global.exception.BadWords;
import com.example.zzan.global.util.S3Uploader;
import com.example.zzan.mypage.dto.MyPageResponseDto;
import com.example.zzan.mypage.dto.MypageChangeDto;
import com.example.zzan.mypage.dto.RelatedUserResponseDto;
import com.example.zzan.mypage.dto.UserSearchDto;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.zzan.global.exception.ExceptionEnum.*;

@Service
@RequiredArgsConstructor
public class MyPageService {
    private final UserRepository userRepository;
    private final S3Uploader s3Uploader;
    private final UserHistoryRepository userHistoryRepository;
    private final FollowRepository followRepository;
    private final BlockListRepository blockListRepository;

    @Transactional
    public ResponseDto<MypageChangeDto> saveMyPage(MultipartFile userImage, String username, String email, String introduction) throws IOException {
        String storedFileName = null;

        File originalImageFile = convertMultipartFileToFile(userImage);

        if (originalImageFile != null) {
            String fileExtension = getFileExtension(userImage.getOriginalFilename());
            if (fileExtension.equalsIgnoreCase("jpeg") || fileExtension.equalsIgnoreCase("jpg") || fileExtension.equalsIgnoreCase("png")) {
                storedFileName = s3Uploader.compressAndUpload(originalImageFile, "mainImage", 250, 250);
            } else {
                storedFileName = s3Uploader.upload(originalImageFile, "mainImage");
            }
        }

        User myPage = findUser(email);
        if (myPage != null) {
            if (username != null && !username.trim().isEmpty()) {
                if (hasBadWord(username)) {
                    throw new ApiException(NOT_ALLOWED_USERNAME);
                }
                if (userRepository.findByUsername(username).isPresent()) {
                    throw new ApiException(USER_DUPLICATION);
                }
                myPage.username(username);
            }
            if (storedFileName != null) {
                myPage.UserImgurl(storedFileName);
            }
            if (introduction != null && !introduction.trim().isEmpty()) {
                if (hasBadWord(introduction)) {
                    throw new ApiException(NOT_ALLOWED_INTRODUCTION);
                }
                myPage.setIntroduction(introduction);
            }

            userRepository.save(myPage);
        } else {
            throw new ApiException(ROOM_NOT_FOUND);
        }
        return ResponseDto.setSuccess("Mypage has been saved", new MypageChangeDto(myPage));
    }

    @Transactional
    public ResponseDto<MyPageResponseDto> getUserInfo(User user) {
        Pageable topTwenty = PageRequest.of(0, 20);

        List<UserHistory> userHistories = userHistoryRepository.findTop20ByHostUserOrEnterUserOrderByCreatedAtDesc(user, topTwenty);
        List<Follow> follows = followRepository.findAllByFollowerUserOrderByCreatedAtDesc(user);
        List<BlockList> blockLists = blockListRepository.findAllByBlockListingUserOrderByCreatedAtDesc(user);

        List<UserHistoryDto> userHistoryDtos = new ArrayList<>();
        List<FollowResponseDto> followResponseDtos = new ArrayList<>();
        List<BlockListDto> blockListDtos = new ArrayList<>();
        User myPage = findUser(user.getEmail());

        for (UserHistory userHistory : userHistories) {
            if (userHistory.getGuestUser() != null) {
                String metUser = "";
                String metUserImage = "";
                Long metUserId = null;
                if (userHistory.getHostUser().getId().equals(user.getId())) {
                    metUserId = userHistory.getGuestUser().getId();
                    metUser = userHistory.getGuestUser().getUsername();
                    metUserImage = userHistory.getGuestUser().getUserImage();
                } else if (!userHistory.getHostUser().getId().equals(user.getId())) {
                    metUserId = userHistory.getHostUser().getId();
                    metUser = userHistory.getHostUser().getUsername();
                    metUserImage = userHistory.getHostUser().getUserImage();
                }
                LocalDateTime metCreatedAt = userHistory.getCreatedAt();

                UserHistoryDto userHistoryDto = new UserHistoryDto(metUserId, metUser, metUserImage, metCreatedAt);
                userHistoryDtos.add(userHistoryDto);
            }
        }

        for (Follow follow : follows) {
            Long followingUserId = follow.getFollowingUser().getId();
            String followingUser = follow.getFollowingUser().getUsername();
            String followingUserImage = follow.getFollowingUser().getUserImage();
            LocalDateTime followCreatedAt = follow.getCreatedAt();

            FollowResponseDto followResponseDto = new FollowResponseDto(followingUserId, followingUser, followingUserImage, followCreatedAt);
            followResponseDtos.add(followResponseDto);
        }

        for (BlockList blockList : blockLists) {
            Long blacklistedUserId = blockList.getBlockListedUser().getId();
            String blacklistedUser = blockList.getBlockListedUser().getUsername();
            String blackUserImage = blockList.getBlockListedUser().getUserImage();
            LocalDateTime BlacklistCreatedAt = blockList.getCreatedAt();

            BlockListDto blockListDto = new BlockListDto(blacklistedUserId, blacklistedUser, blackUserImage, BlacklistCreatedAt);
            blockListDtos.add(blockListDto);
        }
        return ResponseDto.setSuccess("Successfully checked the record.", new MyPageResponseDto(myPage, myPage.getAlcohol(), userHistoryDtos, followResponseDtos, blockListDtos));
    }

    public ResponseDto<RelatedUserResponseDto> UserInfoFromId(Long targetId, User user) {
        Optional<User> optionalUser = userRepository.findById(targetId);
        if (!optionalUser.isPresent()) {
            throw new ApiException(USER_NOT_FOUND);
        }
        User targetUser = optionalUser.get();
        boolean isFollowing = followRepository.findByFollowingUserAndFollowerUser(targetUser, user).isPresent();
        boolean isBlocked = blockListRepository.findByBlockListedUserAndBlockListingUser(targetUser, user).isPresent();

        return ResponseDto.setSuccess("Successfully checked the record.", new RelatedUserResponseDto(targetUser, isFollowing, isBlocked));
    }

    public ResponseDto<List<UserSearchDto>> searchUserByUsername(String username, User currentUser) {
        List<User> users = userRepository.findByUsernameContainingIgnoreCase(username);
        List<UserSearchDto> searchResults = new ArrayList<>();

        for (User user : users) {
            boolean followedByCurrentUser = followRepository.findByFollowingUserAndFollowerUser(user, currentUser).isPresent();
            boolean blockedByCurrentUser = blockListRepository.findByBlockListedUserAndBlockListingUser(user, currentUser).isPresent();

            UserSearchDto userSearchDto = new UserSearchDto(user, followedByCurrentUser, blockedByCurrentUser);
            searchResults.add(userSearchDto);
        }
        return ResponseDto.setSuccess("User search results.", searchResults);
    }

    public User findUser(String email) {
        Optional<User> optionalUser = userRepository.findUserByEmail(email);
        return optionalUser.orElse(null);
    }

    private boolean hasBadWord(String input) {
        for (String badWord : BadWords.koreaWord) {
            if (input.contains(badWord)) {
                return true;
            }
        }
        return false;
    }

    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf(".");
        if (dotIndex != -1 && dotIndex < filename.length() - 1) {
            return filename.substring(dotIndex + 1).toLowerCase();
        }
        return "";
    }

    private File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile == null || multipartFile.isEmpty()) {
            return null;
        }
        String originalFileName = multipartFile.getOriginalFilename();
        String ext = originalFileName.substring(originalFileName.lastIndexOf(".")).toLowerCase();

        if (!ext.equalsIgnoreCase(".png") && !ext.equalsIgnoreCase(".jpg") && !ext.equalsIgnoreCase(".jpeg") && !ext.equalsIgnoreCase(".gif")) {
            throw new ApiException(INVALID_FILE);
        }
        String uuidFileName = UUID.randomUUID().toString().replace("-", "") + ext;

        File convertFile = new File(uuidFileName);
        try (FileOutputStream fos = new FileOutputStream(convertFile)) {
            fos.write(multipartFile.getBytes());
        }
        return convertFile;
    }
}