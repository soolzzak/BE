package com.example.zzan.room.service;

import com.example.zzan.global.dto.ResponseDto;
import com.example.zzan.global.exception.ApiException;
import com.example.zzan.room.dto.RoomRequestDto;
import com.example.zzan.room.dto.RoomResponseDto;
import com.example.zzan.room.entity.Room;
import com.example.zzan.room.repository.RoomRepository;
import com.example.zzan.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.zzan.global.exception.ExceptionEnum.*;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    @Transactional
    public ResponseDto<RoomResponseDto> createRoom(RoomRequestDto roomRequestDto, User user) {
        Room room = new Room(roomRequestDto, user);
        roomRepository.save(room);
        return ResponseDto.setSuccess("방을 생성하였습니다.", new RoomResponseDto(room));
    }

    @Transactional(readOnly = true)
    public ResponseDto<List<RoomResponseDto>> getRooms (Pageable pageable) {
        List<RoomResponseDto> roomList = roomRepository.findAll(pageable).getContent().stream().map(RoomResponseDto::new).collect(Collectors.toList());
        return ResponseDto.setSuccess("전체 조회 성공", roomList);
    }

    @Transactional
    public ResponseDto<RoomResponseDto> updateRoom (Long roomId, RoomRequestDto roomRequestDto, User user) {
        Room room = roomRepository.findById(roomId).orElseThrow(
                () -> new ApiException(ROOM_NOT_FOUND)
        );
        if (!room.getUser().getId().equals(user.getId())) {
            throw new ApiException(UNAUTHORIZED);
        }
        room.update(roomRequestDto);

//        if (image != null && !image.isEmpty()) {
//            s3Service.delete(room.getRoomImage());
//            String imageUrl = s3Service.uploadFile(image);
//            room.setRoomImage(imageUrl);
//        }

        roomRepository.save(room);
        return ResponseDto.setSuccess("방을 수정하였습니다.", new RoomResponseDto(room));
    }

    @Transactional
    public ResponseDto<RoomResponseDto> deleteRoom(Long roomId, User user) {
        Room room = roomRepository.findById(roomId).orElseThrow(
                () -> new ApiException(ROOM_NOT_FOUND)
        );

        if (room.getUser().getId().equals(user.getId())) {
            roomRepository.delete(room);
            return ResponseDto.setSuccess("방을 삭제하였습니다.", null);
        } else {
            throw new ApiException(UNAUTHORIZED);
        }
    }
}