package com.example.zzan.mypage.controller;

import java.io.IOException;

import com.example.zzan.mypage.dto.MyPageResponseDto;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

<<<<<<< HEAD
import com.example.zzan.global.security.UserDetailsImpl;
import com.example.zzan.mypage.dto.MyPageResponseDto;
=======
>>>>>>> 9ab9d9e6494b51466b7b2ad6ffc31896e74f204b
import com.example.zzan.mypage.service.MyPageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class myPageController {

	private final MyPageService myPageService; // 의존성 주입 필요


	@ResponseBody
	@PutMapping(value="/mypage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public MyPageResponseDto saveimg(@RequestParam(value="image", required=false) MultipartFile image, @RequestParam(value="nickname", required=false) String nickname,@AuthenticationPrincipal UserDetailsImpl userDetails) throws
		IOException {
		// Long imgId = myPageService.saveMyPage(image, nickname);
		return myPageService.saveMyPage(image, nickname,userDetails.getUser().getEmail());
	}

	//consumes = MediaType.MULTIPART_FORM_DATA_VALUE는 클라이언트가 서버로 데이터를 전송할 때 사용하는 형식을 나타내며 폼데이터로 올때 사용

//multipart/form-data는 HTTP 요청의 Content-Type 필드에서 사용되는 MIME 형식의 하나로, 주로 파일과 같은 비텍스트 데이터를 포함한 폼 데이터를 전송하는 데 사용
//폼데이터란 텍스트필드,체크박스등 다양한 입력요소를 입력할때 전달되는 데이터 형태
}

