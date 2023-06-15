// package com.example.zzan.room;
//
// import com.example.zzan.global.dto.ResponseDto;
// import com.example.zzan.global.jwt.JwtUtil;
// import com.example.zzan.room.dto.RoomRequestDto;
// import com.example.zzan.room.dto.RoomResponseDto;
// import com.example.zzan.room.entity.Category;
// import com.example.zzan.room.entity.GenderSetting;
// import com.example.zzan.room.repository.RoomRepository;
// import com.example.zzan.room.service.RoomService;
// import com.example.zzan.user.entity.User;
// import org.junit.jupiter.api.*;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.http.MediaType;
// import org.springframework.mock.web.MockMultipartFile;
// import org.springframework.transaction.annotation.Transactional;
//
// import java.io.IOException;
// import java.text.ParseException;
// import java.text.SimpleDateFormat;
// import java.util.Date;
//
// import static org.junit.jupiter.api.Assertions.assertEquals;
//
// @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// @Transactional
// @ExtendWith(MockitoExtension.class)
// public class CreateRoomTest {
//
// 	@Autowired
// 	RoomService roomService;
//
//
// 	private RoomRequestDto roomRequestDto;
// 	private MockMultipartFile roomImage;
//
// 	@BeforeEach
// 	void createRoom() {
// 		roomRequestDto = new RoomRequestDto();
// 		roomRequestDto.setTitle("Room Testing");
// 		roomRequestDto.setIsPrivate(false);
// 		roomRequestDto.setRoomPassword("");
// 		roomRequestDto.setGenderSetting(GenderSetting.MALE);
// 		roomImage = new MockMultipartFile(
// 			"roomImage",
// 			"hello.png",
// 			MediaType.IMAGE_PNG_VALUE,
// 			"Hello, World!".getBytes());
// 		roomRequestDto.setCategory(Category.MOVIE_DRAMA);
// 	}
//
// 	@Nested
// 	@DisplayName("TEST: room 생성")
// 	class createRoom {
// 		@Test
// 		@DisplayName("CASE: room 생성 성공")
// 		void createRoomTest() throws IOException {
// 			User mockUser = new User();
// 			mockUser.setUsername("testUser");
// 			mockUser.setPassword("Rpassword");
//
// 			ResponseDto<RoomResponseDto> responseEntity = roomService.createRoom(roomRequestDto, roomImage, mockUser);
//
// 			Assertions.assertNotNull(responseEntity);
// 			assertEquals(200, responseEntity.getStatus());
// 			assertEquals("방을 생성하였습니다.", responseEntity.getMessage());
//
// 		}
// 	}
// }