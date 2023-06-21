package com.example.zzan.room.service;

import com.example.zzan.blocklist.entity.BlockList;
import com.example.zzan.blocklist.repository.BlockListRepository;
import com.example.zzan.global.dto.ResponseDto;
import com.example.zzan.global.exception.ApiException;
import com.example.zzan.global.exception.BadWords;
import com.example.zzan.global.util.S3Uploader;
import com.example.zzan.room.dto.RoomCheckResponseDto;
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
import com.example.zzan.user.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final UserHistoryRepository userHistoryRepository;
    private final BlockListRepository blockListRepository;
    private final SseService sseService;
    private final S3Uploader s3Uploader;
    private static final Logger log = LoggerFactory.getLogger(RoomService.class);

    @Transactional
    public ResponseDto<RoomResponseDto> createRoom(RoomRequestDto roomRequestDto, MultipartFile roomImage, User user) throws IOException {
        String roomImageUrl = null;
        Room room = new Room(roomRequestDto, user);
        room.setRoomCapacity(0);

        String roomTitle = roomRequestDto.getTitle();
        if (hasBadWord(roomTitle)) {
            throw  new ApiException(NOT_ALLOWED_ROOMTITLE);
        }

        if (roomRequestDto.getIsPrivate() && (roomRequestDto.getRoomPassword() == null || roomRequestDto.getRoomPassword().isEmpty())) {
            throw  new ApiException(REQUIRE_PASSWORD);
        }

        user.setRoomTitle(roomTitle);
        userRepository.save(user);
        RoomHistory roomHistory = new RoomHistory();
        roomHistory.setRoom(room);

        if (roomImage == null) {
            roomImageUrl = s3Uploader.getRandomImage("Random");
        } else {
            roomImageUrl = s3Uploader.upload(roomImage, "images");
        }
        room.setRoomImage(roomImageUrl);

        roomHistoryRepository.saveAndFlush(roomHistory);
        roomRepository.saveAndFlush(room);
        sseService.notifyFollowers( room, room.getHostUser().getUsername());
        RoomResponseDto roomResponseDto = new RoomResponseDto(room);
        roomResponseDto.setUserList(new HashMap<Long, WebSocketSession>());
        UserListMap.getInstance().getUserMap().put((room.getId()), roomResponseDto);
        return ResponseDto.setSuccess("방을 생성하였습니다.", new RoomResponseDto(room));
    }

    @Transactional(readOnly = true)
    public ResponseDto<Page<RoomResponseDto>> getRooms(Pageable pageable,
                                                       Optional<GenderSetting> genderSettingOptional,
                                                       Optional<Boolean> roomCapacityCheckOptional) {
        Page<Room> roomPage;
        if (genderSettingOptional.isPresent() && roomCapacityCheckOptional.isPresent()) {
            if (roomCapacityCheckOptional.get()) {
                roomPage = roomRepository.findByGenderSettingAndRoomCapacityLessThanAndRoomDeleteIsFalse(genderSettingOptional.get(), 2, pageable);
            } else {
                roomPage = roomRepository.findByGenderSettingAndRoomDeleteIsFalse(genderSettingOptional.get(), pageable);
            }
        } else if (roomCapacityCheckOptional.isPresent() && roomCapacityCheckOptional.get()) {
            roomPage = roomRepository.findByRoomCapacityLessThanAndRoomDeleteIsFalse(2, pageable);
        } else {
            roomPage = roomRepository.findAllByRoomDeleteIsFalse(pageable);
        }
        Page<RoomResponseDto> roomList = roomPage.map(RoomResponseDto::new);
        return ResponseDto.setSuccess("조건에 맞는 방 조회 성공", roomList);
    }

    @Transactional(readOnly = true)
    public ResponseDto<Page<RoomResponseDto>> getRoomsBySettingAndCategory(Category category, Pageable pageable,
                                                                           Optional<GenderSetting> genderSettingOptional,
                                                                           Optional<Boolean> roomCapacityCheckOptional) {
        Page<Room> roomPage;
        if (genderSettingOptional.isPresent() && roomCapacityCheckOptional.isPresent() && roomCapacityCheckOptional.get()) {
            roomPage = roomRepository.findByCategoryAndGenderSettingAndRoomCapacityLessThanAndRoomDeleteIsFalse(category, pageable, genderSettingOptional.get(), 2);
        } else if (genderSettingOptional.isPresent()) {
            roomPage = roomRepository.findByCategoryAndGenderSettingAndRoomDeleteIsFalse(category, pageable, genderSettingOptional.get());
        } else if (roomCapacityCheckOptional.isPresent() && roomCapacityCheckOptional.get()) {
            roomPage = roomRepository.findByCategoryAndRoomCapacityLessThanAndRoomDeleteIsFalse(category, pageable, 2);
        } else {
            roomPage = roomRepository.findByCategoryAndRoomDeleteIsFalse(category, pageable);
        }
        Page<RoomResponseDto> roomList = roomPage.map(RoomResponseDto::new);
        return ResponseDto.setSuccess("Successfully matched room lookups.", roomList);
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

        if (roomImage == null) {
            roomImageUrl = s3Uploader.getRandomImage("Random");
        } else {
            roomImageUrl = s3Uploader.upload(roomImage, "mainImage");
        }
        room.setRoomImage(roomImageUrl);

        String roomTitle = roomRequestDto.getTitle();
        if (hasBadWord(roomTitle)) {
            throw  new ApiException(NOT_ALLOWED_ROOMTITLE);
        }

        roomRepository.save(room);
        return ResponseDto.setSuccess("Successfully modified the room.", null);
    }

    @Transactional
    public ResponseDto<RoomResponseDto> deleteRoom(Long roomId, User user) {
        Room room = roomRepository.findById(roomId).orElseThrow(
                () -> new ApiException(ROOM_NOT_FOUND)
        );
        if (room.getHostUser().getId().equals(user.getId())) {
            roomRepository.delete(room);
            return ResponseDto.setSuccess("Successfully deleted the room.", null);
        } else {
            throw new ApiException(UNAUTHORIZED_USER);
        }
    }

    private boolean hasBadWord(String input) {
        return BadWords.koreaWord.stream().anyMatch(input::contains);
    }

    @Transactional
    public ResponseDto<RoomCheckResponseDto> checkRoom(Long roomId, User user) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ApiException(ROOM_NOT_FOUND));

        if (room.getRoomCapacity() >= 2) {
            throw new ApiException(ROOM_ALREADY_FULL);
        }

        List<BlockList> userBlockListing = blockListRepository.findAllByBlockListingUserOrderByCreatedAtDesc(user);
        for (BlockList blockList : userBlockListing) {
            if (room.getHostUser().equals(blockList.getBlockListedUser())) {
                throw new ApiException(BLOCKED_USER);
            }
        }

        List<BlockList> userBlockListed = blockListRepository.findAllByBlockListedUserOrderByCreatedAtDesc(user);
        for (BlockList blockList : userBlockListed) {
            if (room.getHostUser().equals(blockList.getBlockListingUser())) {
                throw new ApiException(BLOCKED_USER);
            }
        }
        RoomCheckResponseDto roomCheckResponseDto = new RoomCheckResponseDto(room.getRoomCapacity());
        return ResponseDto.setSuccess("Room check passed successfully.", roomCheckResponseDto);
    }

    @Transactional
    public ResponseDto<?> passwordCheck(Long roomId, User user, String roomPassword) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ApiException(ROOM_NOT_FOUND));

        if (room.getIsPrivate()) {
            if (!room.getRoomPassword().equals(roomPassword)) {
                throw new ApiException(PASSWORD_NOT_MATCH);
            }
        }
        return ResponseDto.setSuccess("The password matches the room password");
    }

    @Transactional
    public RoomResponseDto enterRoom(Long roomId, User user) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ApiException(ROOM_NOT_FOUND));

        room.setRoomCapacity(room.getRoomCapacity() + 1);
        roomRepository.save(room);
        if (room.getTitle() != null) {
            user.setRoomTitle(room.getTitle());
            userRepository.save(user);
        } else {
            throw new ApiException(ROOM_NOT_FOUND);
        }

        if (!room.getHostUser().getId().equals(user.getId())) {
            UserHistory userHistory = new UserHistory();
            userHistory.setGuestUser(user);
            userHistory.setHostUser(room.getHostUser());
            RoomHistory roomHistory = new RoomHistory(room);
            userHistory.setRoom(roomHistory.getRoom());
            userHistoryRepository.save(userHistory);
        }
        return new RoomResponseDto(room);
    }

    @Transactional
    public ResponseDto leaveRoom(Long roomId, User user) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ApiException(ROOM_NOT_FOUND));
        if (room.getHostUser().getId().equals(user.getId())) {
            room.roomDelete(true);
        } else if (!room.getHostUser().getId().equals(user.getId())) {
            // room.setRoomCapacity(room.getRoomCapacity() - 1);
            // roomRepository.save(room);s
        }
        return ResponseDto.setSuccess("Successfully exited the room", null);
    }

    @Transactional(readOnly = true)
    public ResponseDto<Page<RoomResponseDto>> getSearchedRoom(Pageable pageable, String title) {
        Page<Room> rooms = roomRepository.findAllByTitleContainingAndRoomDeleteIsFalse(title, pageable);
        Page<RoomResponseDto> roomList = rooms.map(RoomResponseDto::new);
        return ResponseDto.setSuccess("Search success.", roomList);
    }
}