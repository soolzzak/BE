package com.example.zzan.room.controller;

import com.example.zzan.global.dto.ResponseDto;
import com.example.zzan.global.security.UserDetailsImpl;
import com.example.zzan.room.dto.RoomRequestDto;
import com.example.zzan.room.dto.RoomResponseDto;
import com.example.zzan.room.entity.Category;
import com.example.zzan.room.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/api")
public class RoomController {
    private final RoomService roomService;

    @GetMapping("/main")
    public ResponseDto<List<RoomResponseDto>> getRooms(@PageableDefault(page = 0, size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return roomService.getRooms(pageable);
    }

    @GetMapping("/rooms")
    public ResponseDto<List<RoomResponseDto>> chooseCategory(@RequestParam("category") Category category, Pageable pageable) {
        return roomService.chooseCategory(category, pageable);
    }

    @PostMapping(value = "/room", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public ResponseDto<RoomResponseDto> createRoom(@RequestPart(value = "roomRequestDto") RoomRequestDto roomRequestDto,
                                                   @RequestPart(value = "roomImage", required = false) MultipartFile roomImage,
                                                   @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        return roomService.createRoom(roomRequestDto, roomImage, userDetails.getUser());
    }

    @PutMapping(value = "/room/{roomId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public ResponseDto<RoomResponseDto> updateRoom(@PathVariable Long roomId,
                                                   @RequestPart(value = "roomRequestDto") RoomRequestDto roomRequestDto,
                                                   @RequestPart(value = "roomImage", required = false) MultipartFile roomImage,
                                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return roomService.updateRoom(roomId, roomRequestDto, roomImage, userDetails.getUser());
    }

    @DeleteMapping("/room/{roomId}")
    public ResponseDto<RoomResponseDto> deleteRoom(@PathVariable Long roomId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return roomService.deleteRoom(roomId, userDetails.getUser());
    }


    @GetMapping("/room/{roomId}")
    public ResponseDto<RoomResponseDto>getOneRoom(@PathVariable Long roomId,@AuthenticationPrincipal UserDetailsImpl userDetails){
        return roomService.getOneRoom(roomId,userDetails.getUser());
    }
}
