package com.example.zzan.roomreport.repository;

import com.example.zzan.roomreport.entity.UserReport;
import com.example.zzan.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserReportRepository extends JpaRepository<UserReport, Long> {

	@Query("SELECT r FROM UserReport r WHERE r.hostUser = :user OR r.enterUser = :user ORDER BY r.createdAt DESC")
	List<UserReport> findTop3ByHostUserOrEnterUserOrderByCreatedAtDesc(@Param("user") User user, Pageable pageable);
}
