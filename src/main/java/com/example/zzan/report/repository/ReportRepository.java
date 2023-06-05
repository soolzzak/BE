package com.example.zzan.report.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.zzan.report.entity.Report;
import com.example.zzan.user.entity.User;

public interface ReportRepository extends JpaRepository<Report,Long> {
	Optional<Report> findByReportingUserAndReportedUser(User reportingUser,User reportedUser);
}
