package com.example.zzan.room.service;

import com.example.zzan.global.StatusEnum;
import com.example.zzan.global.dto.BasicResponseDto;
import com.example.zzan.global.dto.ResponseDto;
import com.example.zzan.global.exception.ApiException;
import com.example.zzan.room.dto.RoomRequestDto;
import com.example.zzan.room.dto.RoomResponseDto;
import com.example.zzan.room.entity.Room;
import com.example.zzan.room.repository.RoomRepository;
import com.example.zzan.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.zzan.global.exception.ExceptionEnum.*;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    @Transactional
    public ResponseEntity<BasicResponseDto> createRoom(RoomRequestDto roomRequestDto, User user) {
        Room room = new Room(roomRequestDto, user);
        roomRepository.saveAndFlush(room);

//        if (image != null && !image.isEmpty()) {
//            String fileName = StringUtils.cleanPath(image.getOriginalFilename());
//            room.setImageUrl(fileName);
//        }

        BasicResponseDto basicResponseDto = BasicResponseDto.setSuccess(StatusEnum.OK, "방 생성 성공");
        return ResponseEntity.ok(basicResponseDto);
    }

    @Transactional(readOnly = true)
    public ResponseDto<List<RoomResponseDto>> getRooms (Pageable pageable) {
        List<RoomResponseDto> roomList = roomRepository.findAll(pageable).getContent().stream().map(RoomResponseDto::new).collect(Collectors.toList());
        return ResponseDto.setSuccess("전체 조회 성공", roomList);
    }

    @Transactional
    public ResponseEntity<BasicResponseDto> updateRoom (Long roomId, RoomRequestDto roomRequestDto, User user) {
        Room room = roomRepository.findById(roomId).orElseThrow(
                () -> new ApiException(ROOM_NOT_FOUND)
        );
//        if (!room.getUser().getUserId().equals(user.getUserId())) {
//            throw new ApiException(UNAUTHORIZED);
//        }

        room.update(roomRequestDto);

//        if (image != null && !image.isEmpty()) {
//            s3Service.delete(room.getRoomImage());
//            String imageUrl = s3Service.uploadFile(image);
//            room.setRoomImage(imageUrl);
//        }

        roomRepository.save(room);
        BasicResponseDto basicResponseDto = BasicResponseDto.setSuccess(StatusEnum.OK, "방 수정 성공");
        return ResponseEntity.ok(basicResponseDto);
    }

    @Transactional
    public ResponseEntity<BasicResponseDto> deleteroom(Long roomId, User user) {
        Room room = roomRepository.findById(roomId).orElseThrow(
                () -> new ApiException(ROOM_NOT_FOUND)
        );

//        if (room.getUser().getUserId().equals(user.getUserId())) {
            roomRepository.delete(room);
            BasicResponseDto basicResponseDto = BasicResponseDto.setSuccess(StatusEnum.OK, "방 삭제 성공");
            return ResponseEntity.ok(basicResponseDto);
//        } else {
//            throw new ApiException(UNAUTHORIZED);
//        }
    }

//    private void isUserAdmin(User user) {
//    }
//
//    private void existroom(Long roomId){
//        return RoomRepository.findById(roomId).orElseThrow(
//        () -> new IllegalStateException("해당 게시물이 없습니다."));
//    }
}