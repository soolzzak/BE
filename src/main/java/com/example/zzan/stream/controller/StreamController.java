package com.example.zzan.stream.controller;

import com.example.zzan.stream.dto.StreamRequestDto;
import com.example.zzan.stream.dto.StreamResponseDto;
import com.example.zzan.stream.service.StreamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StreamController {
    private final StreamService streamService;

    // 화상 혼술방 전체 보기 ( 메인페이지 )
    @GetMapping("/main")
    public ResponseEntity<List<StreamResponseDto>> getStreams() {
//        return ResponseEntity.ok();
    }

    // 화상 혼술방 등록
    @PostMapping("/stream")
    public ResponseEntity<StreamResponseDto> createStream(@RequestPart StreamRequestDto streamRequestDto,
                                                          @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                          @RequestPart(value = "image", required = false) MultipartFile multipartFile) throws IOException {
        return ResponseEntity.ok();
    }

    // 화상 혼술방 수정
    @PutMapping("/stream/{streamId}")
    public ResponseEntity<StreamResponseDto> updateStream(@PathVariable Long streamId,
                                                          @RequestPart StreamRequestDto streamRequestDto,
                                                          @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                          @RequestPart(value = "image", required = false) MultipartFile multipartFile) throws IOException {
        return ResponseEntity.ok();
    }

    // 화상 혼술방 삭제
    @DeleteMapping("/stream/{streamId}")
    public ResponseEntity<?> deleteStream(@PathVariable Long streamId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok();
    }

}
