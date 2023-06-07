package com.example.zzan.room.service;

import com.example.zzan.blocklist.entity.BlockList;
import com.example.zzan.blocklist.repository.BlockListRepository;
import com.example.zzan.global.dto.ResponseDto;
import com.example.zzan.global.exception.ApiException;
import com.example.zzan.global.util.BadWords;
import com.example.zzan.mypage.service.S3Uploader;
import com.example.zzan.room.dto.RoomRequestDto;
import com.example.zzan.room.dto.RoomResponseDto;
import com.example.zzan.room.entity.Category;
import com.example.zzan.room.entity.GenderSetting;
import com.example.zzan.room.entity.Room;
import com.example.zzan.room.entity.RoomHistory;
import com.example.zzan.room.repository.RoomHistoryRepository;
import com.example.zzan.room.repository.RoomRepository;
import com.example.zzan.sse.service.SseService;
import com.example.zzan.user.entity.User;
import com.example.zzan.userHistory.entity.UserHistory;
import com.example.zzan.userHistory.repository.UserHistoryRepository;
import com.example.zzan.webRtc.dto.UserListMap;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.socket.WebSocketSession;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static com.example.zzan.global.exception.ExceptionEnum.*;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomHistoryRepository roomHistoryRepository;

    private final UserHistoryRepository userHistoryRepository;
    private final SseService sseService;

    private final S3Uploader s3Uploader;
    private final BlockListRepository blockListRepository;
    private static final Logger log = LoggerFactory.getLogger(RoomService.class);

    @Transactional
    public ResponseDto<RoomResponseDto> createRoom(RoomRequestDto roomRequestDto, MultipartFile roomImage, User user) throws IOException {
        String roomImageUrl = null;
        Room room = new Room(roomRequestDto, user);
        room.setRoomCapacity(1);

        String roomTitle = roomRequestDto.getTitle();

        if (hasBadWord(roomTitle)) {
            return ResponseDto.setBadRequest("방 제목에 사용할 수 없는 단어가 있습니다.");
        }

        if (!roomRequestDto.getIsPrivate()) {
        } else {
            String roomPassword = roomRequestDto.getRoomPassword();
            if (roomPassword == null || roomPassword.isEmpty())
                return ResponseDto.setBadRequest("방 비밀번호를 설정해주세요.");
        }

        RoomHistory roomHistory = new RoomHistory();
        roomHistory.setRoom(room);

        if (roomImage.isEmpty()) {
            roomImageUrl = s3Uploader.getRandomImage("Random");
        } else {
            roomImageUrl = s3Uploader.upload(roomImage, "images");
        }

        room.setRoomImage(roomImageUrl);
        roomHistoryRepository.saveAndFlush(roomHistory);
        roomRepository.saveAndFlush(room);
        RoomResponseDto roomResponseDto = new RoomResponseDto(room);
        roomResponseDto.setUserList(new HashMap<Long, WebSocketSession>());
        UserListMap.getInstance().getUserMap().put((room.getId()), roomResponseDto);

        return ResponseDto.setSuccess("방을 생성하였습니다.", new RoomResponseDto(room));
    }

    @Transactional(readOnly = true)
    public ResponseDto<Page<RoomResponseDto>> getRooms(Pageable pageable) {
        Page<Room> roomPage = roomRepository.findAllByRoomDeleteIsFalse(pageable);
        Page<RoomResponseDto> roomList = roomPage.map(RoomResponseDto::new);
        return ResponseDto.setSuccess("전체 조회 성공", roomList);
    }

    @Transactional(readOnly = true)
    public ResponseDto<Page<RoomResponseDto>> chooseCategory(Category category, Pageable pageable) {
        Page<Room> roomPage = roomRepository.findAllByCategoryAndRoomDeleteIsFalse(category, pageable);
        Page<RoomResponseDto> roomList = roomPage.map(RoomResponseDto::new);
        return ResponseDto.setSuccess("카테고리 검색 성공", roomList);
    }

    @Transactional
    public ResponseDto<RoomResponseDto> updateRoom(Long roomId, RoomRequestDto roomRequestDto, MultipartFile roomImage, User user) throws IOException {
        String roomImageUrl = null;
        Room room = roomRepository.findById(roomId).orElseThrow(
                () -> new ApiException(ROOM_NOT_FOUND)
        );
        if (!room.getHostUser().getId().equals(user.getId())) {
            throw new ApiException(UNAUTHORIZED_USER);
        }

        room.update(roomRequestDto);
        if (roomImage.isEmpty()) {
            roomImageUrl = s3Uploader.getRandomImage("Random");
        } else {
            s3Uploader.removeNewFile(new File(room.getRoomImage()));
            roomImageUrl = s3Uploader.upload(roomImage, "images");
        }
        room.setRoomImage(roomImageUrl);

        String roomTitle = roomRequestDto.getTitle();
        if (hasBadWord(roomTitle)) {
            return ResponseDto.setBadRequest("방 제목에 사용할 수 없는 단어가 있습니다.");
        }

        roomRepository.save(room);
        return ResponseDto.setSuccess("방을 수정하였습니다.", null);
    }

    @Transactional
    public ResponseDto<RoomResponseDto> deleteRoom(Long roomId, User user) {
        Room room = roomRepository.findById(roomId).orElseThrow(
                () -> new ApiException(ROOM_NOT_FOUND)
        );

        if (room.getHostUser().getId().equals(user.getId())) {
            roomRepository.delete(room);
            return ResponseDto.setSuccess("방을 삭제하였습니다.", null);
        } else {
            throw new ApiException(UNAUTHORIZED_USER);
        }
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
    public RoomResponseDto enterRoom(Long roomId, User user) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ApiException(ROOM_NOT_FOUND));

        if (room.getRoomCapacity() >= 3) {
            throw new ApiException(ROOM_ALREADY_FULL);
        }

        List<BlockList> userBlockLists = blockListRepository.findAllByBlockListingUserOrderByCreatedAtDesc(user);

        for (BlockList blockList : userBlockLists) {
            if (room.getHostUser().equals(blockList.getBlockListedUser())) {
                throw new ApiException(BLOCKED_USER);
            }
        }

        room.setRoomCapacity(room.getRoomCapacity() + 1);
        roomRepository.save(room);

        UserHistory userHistory = new UserHistory();
        userHistory.setHostUser(room.getHostUser());
        userHistory.setGuestUser(user);

        RoomHistory roomHistory = new RoomHistory(room);
        userHistory.setRoom(roomHistory.getRoom());

        userHistoryRepository.save(userHistory);

        return new RoomResponseDto(room);
    }

    @Transactional
    public ResponseDto leaveRoom(Long roomId, User user) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ApiException(ROOM_NOT_FOUND));

        if (room.getHostUser().getId().equals(user.getId())) {
            room.roomDelete(true);
        } else if (!room.getHostUser().getId().equals(user.getId())) {
            room.setRoomCapacity(room.getRoomCapacity() - 1);
            roomRepository.save(room);
        }

        return ResponseDto.setSuccess("방나가기 성공", null);
    }

    @Transactional(readOnly = true)
    public ResponseDto<Page<RoomResponseDto>> getSearchedRoom(Pageable pageable, String title) {
        Page<Room> rooms = roomRepository.findAllByTitleContainingAndRoomDeleteIsFalse(title, pageable);
        Page<RoomResponseDto> roomList = rooms.map(RoomResponseDto::new);
        return ResponseDto.setSuccess("검색 성공", roomList);
    }

    @Transactional(readOnly = true)
    public ResponseDto<Page<RoomResponseDto>> getRoomsBySetting(Pageable pageable,
                                                                Optional<GenderSetting> genderSettingOptional,
                                                                Optional<Boolean> roomCapacityCheckOptional) {
        Page<Room> roomPage;
        if (genderSettingOptional.isPresent() && roomCapacityCheckOptional.isPresent() && roomCapacityCheckOptional.get()) {
            roomPage = roomRepository.findByGenderSettingAndRoomCapacityLessThan(pageable, genderSettingOptional.get(), 2);
        } else if (genderSettingOptional.isPresent()) {
            roomPage = roomRepository.findByGenderSetting(pageable, genderSettingOptional.get());
        } else if (roomCapacityCheckOptional.isPresent() && roomCapacityCheckOptional.get()) {
            roomPage = roomRepository.findByRoomCapacityLessThan(pageable, 2);
        } else {
            roomPage = roomRepository.findAll(pageable);
        }
        Page<RoomResponseDto> roomList = roomPage.map(RoomResponseDto::new);
        return ResponseDto.setSuccess("조건에 맞는 방 조회 성공", roomList);
    }
}