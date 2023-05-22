package com.example.zzan.mypage.repository;

import com.example.zzan.mypage.entity.MyPage;
import org.springframework.data.jpa.repository.JpaRepository;

/*
 * JpaRepository 상속 -> 자동으로 빈 등록 (@Repository 안 달아도 됨)
 * */
public interface MyPageRepository extends JpaRepository<MyPage, Long> {   // 인터페이스 다중 상속

	// List<MyPage> findByNickname(String month);
}