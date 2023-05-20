package com.example.zzan.stream.service;

import com.example.zzan.stream.dto.StreamRequestDto;
import com.example.zzan.stream.dto.StreamResponseDto;
import com.example.zzan.stream.entity.Stream;
import com.example.zzan.stream.repository.StreamRepository;
import com.example.zzan.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class StreamService {

    private final StreamRepository streamRepository;

    @Transactional
    public ResponseEntity createStream(StreamRequestDto streamRequestDto, User user, MultipartFile multipartFile) throws IOException {
        Stream stream = new Stream(streamRequestDto);


    }

//    @Transactional(readOnly = true)
//    public ResponseEntity getStreams (User user, Pageable pageable) {
//    }

//    @Transactional
//    public ResponseEntity updateStream (Long streamId, StreamResponseDto streamResponseDto, User user, MultipartFile imageFile) throw IOException{
//
//    }

//    @Transactional
//    public ResponseEntity deleteStream (Long streamId, User user) {
//
//    }

//    private void isUserAdmin(User user) {
//    }

//    private void existStream(Long streamId){
//        return StreamRepository.findById(streamId).orElseThrow(
//        () -> new IllegalStateException("해당 게시물이 없습니다."));
//    }
}
