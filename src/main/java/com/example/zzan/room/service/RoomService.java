package com.example.zzan.room.service;

import com.example.zzan.global.dto.ResponseDto;
import com.example.zzan.global.exception.ApiException;
import com.example.zzan.mypage.service.S3Uploader;
import com.example.zzan.room.dto.RoomRequestDto;
import com.example.zzan.room.dto.RoomResponseDto;
import com.example.zzan.room.entity.Room;
import com.example.zzan.room.repository.RoomRepository;
import com.example.zzan.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.zzan.global.exception.ExceptionEnum.ROOM_NOT_FOUND;
import static com.example.zzan.global.exception.ExceptionEnum.UNAUTHORIZED;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final S3Uploader s3Uploader;

    @Transactional
    public ResponseDto<RoomResponseDto> createRoom(RoomRequestDto roomRequestDto, MultipartFile image, User user) {
        String imageUrl;
        try {
            imageUrl = s3Uploader.upload(image, "dirName");
        } catch (IOException e) {
            return ResponseDto.setBadRequest("이미지를 업로드해주세요.");
        }

        Room room = new Room(roomRequestDto, user);

        if (!roomRequestDto.getIsPrivate()) {
        } else {
            String roomPassword = roomRequestDto.getRoomPassword();
            if (roomPassword == null || roomPassword.isEmpty())
                return ResponseDto.setBadRequest("방 비밀번호를 설정해주세요.");
        }
        room.setImage(imageUrl);
        roomRepository.save(room);
        return ResponseDto.setSuccess("방을 생성하였습니다.", new RoomResponseDto(room));
    }

    @Transactional(readOnly = true)
    public ResponseDto<List<RoomResponseDto>> getRooms(Pageable pageable) {
        List<RoomResponseDto> roomList = roomRepository.findAll(pageable).getContent().stream().map(RoomResponseDto::new).collect(Collectors.toList());
        return ResponseDto.setSuccess("전체 조회 성공", roomList);
    }

    @Transactional
    public ResponseDto<RoomResponseDto> updateRoom(Long roomId, RoomRequestDto roomRequestDto, MultipartFile image, User user) {
        Room room = roomRepository.findById(roomId).orElseThrow(
                () -> new ApiException(ROOM_NOT_FOUND)
        );
        if (!room.getUser().getId().equals(user.getId())) {
            throw new ApiException(UNAUTHORIZED);
        }
        room.update(roomRequestDto);

        if (image != null && !image.isEmpty()) {
            if (room.getImage() != null) {
                s3Uploader.removeNewFile(new File(room.getImage()));
            }

            try {
                String imageUrl = s3Uploader.upload(image, "dirName");
                room.setImage(imageUrl);
            } catch (IOException e) {
                return ResponseDto.setBadRequest("이미지를 업로드해주세요.");
            }
        }

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