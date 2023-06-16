//package com.example.zzan.room;
//
//import com.example.zzan.global.dto.ResponseDto;
//import com.example.zzan.global.exception.ApiException;
//import com.example.zzan.global.jwt.JwtUtil;
//import com.example.zzan.room.dto.RoomRequestDto;
//import com.example.zzan.room.dto.RoomResponseDto;
//import com.example.zzan.room.entity.Category;
//import com.example.zzan.room.entity.GenderSetting;
//import com.example.zzan.room.entity.Room;
//import com.example.zzan.room.repository.RoomRepository;
//import com.example.zzan.room.service.RoomService;
//import com.example.zzan.user.entity.Gender;
//import com.example.zzan.user.entity.User;
//import com.example.zzan.user.entity.UserRole;
//import org.junit.jupiter.api.*;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.io.IOException;
//import java.util.Date;
//
//import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@Transactional
//@ExtendWith(MockitoExtension.class)
//public class UpdateRoomTest {
//    @Autowired
//    RoomService roomService;
//
//    @Autowired
//    JwtUtil jwtUtil;
//
//    @Autowired
//    RoomRepository roomRepository;
//
//    private RoomRequestDto roomRequestDto;
//    private MockMultipartFile roomImage;
//    private Long roomId;
//
//    @BeforeEach
//    void updateRoom() {
//        roomRequestDto = new RoomRequestDto();
//        roomRequestDto.setTitle("Room Title");
//        roomRequestDto.setIsPrivate(false);
//        roomRequestDto.setRoomPassword("");
//        roomRequestDto.setGenderSetting(GenderSetting.MALE);
//        roomImage = new MockMultipartFile(
//                "roomImage",
//                "소주이미지.png",
//                MediaType.IMAGE_PNG_VALUE,
//                "소주이미지".getBytes());
//        roomRequestDto.setCategory(Category.MOVIE_DRAMA);
//
//        User mockUser = new User();
//        mockUser.setUsername("testUser");
//        mockUser.setPassword("testPassword");
//        mockUser.setBirthday(new Date());
//        mockUser.setEmail("test@gmail.com");
//        mockUser.setGender(Gender.MALE);
//        mockUser.setRole(UserRole.USER);
//    }
//
//    @Nested
//    @DisplayName("TEST: room 수정")
//    class updateRoom {
//        @Test
//        @DisplayName("CASE: room 제목 수정 성공")
//        void updateRoomTest() throws IOException {
//            Room room = roomRepository.findById(roomId).orElse(null);
//            assertNotNull(room);
//
//            roomRequestDto.setTitle("New Room Title");
//
//            User mockUser = new User();
//            mockUser.setUsername("testUser");
//            mockUser.setPassword("testPassword");
//            mockUser.setBirthday(new Date());
//            mockUser.setEmail("test@gmail.com");
//            mockUser.setGender(Gender.MALE);
//            mockUser.setRole(UserRole.USER);
//
//            ResponseDto<RoomResponseDto> responseEntity = roomService.updateRoom(room.getId(), roomRequestDto, roomImage, mockUser);
//            assertNotNull(responseEntity);
//            assertEquals(200, responseEntity.getStatus());
//            assertEquals("방 정보를 수정하였습니다.", responseEntity.getMessage());
//
//            RoomResponseDto roomResponseDto = responseEntity.getData();
//            assertNotNull(roomResponseDto);
//            assertEquals(mockUser.getUsername(), roomResponseDto.getUsername());
//        }
//    }
//
//    @Nested
//    @DisplayName("TEST: room 생성 실패")
//    class roomCreateFail {
//        @Test
//        @DisplayName("CASE: roomTitle 사용불가 단어 사용")
//        void badRoomTitle() throws IOException {
//            User mockUser = new User();
//            mockUser.setUsername("testUser");
//            mockUser.setPassword("testPassword");
//            mockUser.setBirthday(new Date());
//            mockUser.setEmail("test@gmail.com");
//            mockUser.setGender(Gender.MALE);
//            mockUser.setRole(UserRole.USER);
//
//            roomRequestDto.setTitle("개새끼");
//
//            Assertions.assertThrows(ApiException.class, () -> {
//                roomService.createRoom(roomRequestDto, roomImage, mockUser);
//            }, "NOT_ALLOWED_ROOMTITLE");
//        }
//        @Test
//        @DisplayName("CASE: room is private with no password")
//        void privateRoomWithNoPassword() throws IOException {
//            User mockUser = new User();
//            mockUser.setUsername("testUser");
//            mockUser.setPassword("testPassword");
//            mockUser.setBirthday(new Date());
//            mockUser.setEmail("test@gmail.com");
//            mockUser.setGender(Gender.MALE);
//            mockUser.setRole(UserRole.USER);
//
//            roomRequestDto.setIsPrivate(true);
//            roomRequestDto.getRoomPassword();
//
//            Assertions.assertThrows(ApiException.class, () -> {
//                roomService.createRoom(roomRequestDto, roomImage, mockUser);
//            }, "REQUIRE_PASSWORD");
//        }
//    }
//}
