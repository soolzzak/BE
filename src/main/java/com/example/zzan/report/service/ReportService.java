package com.example.zzan.report.service;

import com.example.zzan.global.dto.ResponseDto;
import com.example.zzan.global.exception.ApiException;
import com.example.zzan.report.dto.ReportRequestDto;
import com.example.zzan.report.entity.Report;
import com.example.zzan.report.repository.ReportRepository;
import com.example.zzan.user.entity.User;
import com.example.zzan.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.example.zzan.global.exception.ExceptionEnum.*;

@RequiredArgsConstructor
@Service
public class ReportService {

	private final ReportRepository reportRepository;
	private final UserRepository userRepository;

	@Transactional
	public ResponseDto userReport(Long userId, ReportRequestDto reportRequestDto,User user) {

		Optional<User> reportedUserOptional = userRepository.findById(userId);

		User reportedUser = reportedUserOptional.orElseThrow(() -> new ApiException(TARGET_USER_NOT_FOUND));

		if(user.getId().equals(reportedUser.getId())){
			throw new ApiException(NOT_ALLOWED_SELF_REPORT);
		}

		Optional<Report> existingReport = reportRepository.findByReportingUserAndReportedUser(user,reportedUser);
		if (existingReport.isPresent()) {
			return ResponseDto.setSuccess("Already reported user.");
		}

		int pointsToAdd;
		switch (reportRequestDto.getReportKind()) {
			case "광고/사기":
				pointsToAdd = 1;
				break;
			case "금지된 표현":
				pointsToAdd = 2;
				break;
			case "욕설":
				pointsToAdd = 2;
				break;
			case "음담패설":
				pointsToAdd = 3;
				break;
			case "노출":
				pointsToAdd = 5;
				break;
			case "기타":
				pointsToAdd = 0;
				break;
			default:
				throw new ApiException(REPORT_NOT_REASONABLE);
		}

		reportedUser.addReportPoints(pointsToAdd);
		Report report=new Report(reportedUser,reportRequestDto,user);
		reportRepository.save(report);

		return ResponseDto.setSuccess("Your report has been processed.");
	}
}
