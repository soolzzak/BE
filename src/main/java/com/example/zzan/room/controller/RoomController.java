package com.example.zzan.room.controller;

import com.example.zzan.global.dto.ResponseDto;
import com.example.zzan.global.security.UserDetailsImpl;
import com.example.zzan.room.dto.RoomRequestDto;
import com.example.zzan.room.dto.RoomResponseDto;
import com.example.zzan.room.entity.Category;
import com.example.zzan.room.entity.GenderSetting;
import com.example.zzan.room.service.RoomService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Tag(name = "RoomController", description = "혼술방 파트")
@Slf4j
@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/api")
public class RoomController {
    private final RoomService roomService;

    @GetMapping("/main")
    public ResponseDto<Page<RoomResponseDto>> getRooms(@PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                                                       @RequestParam(required = false) Boolean roomCapacityCheck
    ) {
        Optional<Boolean> roomCapacityCheckOptional = Optional.ofNullable(roomCapacityCheck);

        return roomService.getRooms(pageable,roomCapacityCheckOptional);
    }

    @GetMapping("/rooms")
    public ResponseDto<Page<RoomResponseDto>> getRoomsBySettingAndCategory(
            @RequestParam("category") Category category,
            Pageable pageable,
            @RequestParam(required = false) GenderSetting genderSetting,
            @RequestParam(required = false) Boolean roomCapacityCheck
    ) {
        Optional<GenderSetting> genderSettingOptional = Optional.ofNullable(genderSetting);
        Optional<Boolean> roomCapacityCheckOptional = Optional.ofNullable(roomCapacityCheck);

        return roomService.getRoomsBySettingAndCategory(category, pageable, genderSettingOptional, roomCapacityCheckOptional);
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
                                                   @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        return roomService.updateRoom(roomId, roomRequestDto, roomImage, userDetails.getUser());
    }

    @DeleteMapping("/room/{roomId}")
    public ResponseDto<RoomResponseDto> deleteRoom(@PathVariable Long roomId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return roomService.deleteRoom(roomId, userDetails.getUser());
    }

    @GetMapping("/room/{roomId}")
    public RoomResponseDto enterRoom(@PathVariable Long roomId,@AuthenticationPrincipal UserDetailsImpl userDetails){
        return roomService.enterRoom(roomId,userDetails.getUser());
    }

    @DeleteMapping("/room/{roomId}/leave")
    public ResponseDto leaveRoom(@PathVariable Long roomId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return roomService.leaveRoom(roomId, userDetails.getUser());
    }

    @GetMapping("/search")
    public ResponseDto<Page<RoomResponseDto>> getSearchedRoom(@PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable, @RequestParam("title") String title) {
        return roomService.getSearchedRoom(pageable, title);
    }
}
