package com.example.zzan.report.repository;

import com.example.zzan.report.entity.Report;
import com.example.zzan.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report,Long> {
	Optional<Report> findByReportingUserAndReportedUser(User reportingUser,User reportedUser);
}