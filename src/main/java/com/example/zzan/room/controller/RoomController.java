package com.example.zzan.room.controller;

import com.example.zzan.global.dto.BasicResponseDto;
import com.example.zzan.global.dto.ResponseDto;
import com.example.zzan.global.security.UserDetailsImpl;
import com.example.zzan.room.dto.RoomRequestDto;
import com.example.zzan.room.dto.RoomResponseDto;
import com.example.zzan.room.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;

    // 화상 혼술방 전체 보기 ( 메인페이지 )
    @GetMapping("/main")
    public ResponseDto<List<RoomResponseDto>> getRooms (@PageableDefault(page = 0, size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return roomService.getRooms(pageable);
    }

    // 화상 혼술방 선택 보기 ( 화상 대화 시작 )
//    @GetMapping("/room/chat/{roomId}")
//    public ResponseEntity<ResponseDto> getRoom() {
//
//    }

    // 화상 혼술방 등록
    @PostMapping("/room")
    public ResponseDto<RoomResponseDto> createRoom(@RequestBody RoomRequestDto roomRequestDto,
                                                   @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        return roomService.createRoom(roomRequestDto, userDetails.getUser());
    }

    // 화상 혼술방 수정
    @PutMapping("/room/{roomId}")
    public ResponseDto<RoomResponseDto> updateRoom(@PathVariable Long roomId,
                                                       @RequestBody RoomRequestDto roomRequestDto,
                                                       @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return roomService.updateRoom(roomId, roomRequestDto, userDetails.getUser());
    }

    // 화상 혼술방 삭제
    @DeleteMapping("/room/{roomId}")
    public ResponseDto<RoomResponseDto> deleteRoom(@PathVariable Long roomId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return roomService.deleteRoom(roomId, userDetails.getUser());
    }
}
