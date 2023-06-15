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
import static com.example.zzan.global.exception.ExceptionEnum.*;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ReportService {

	private final ReportRepository reportRepository;
	private final UserRepository userRepository;

	@Transactional
	public ResponseDto userReport(Long userId, ReportRequestDto reportRequestDto,User user) {

		Optional<User> reportedUserOptional = userRepository.findById(userId);

		if(!reportedUserOptional.isPresent()){
			throw new ApiException(TARGET_USER_NOT_FOUND);
		}

		User reportedUser = reportedUserOptional.get();

		if(user.getId().equals(reportedUser.getId())){
			throw new ApiException(NOT_ALLOWED_SELF_REPORT);
		}

		Optional<Report> existingReport = reportRepository.findByReportingUserAndReportedUser(user,reportedUser);
		if (existingReport.isPresent()) {
			return ResponseDto.setSuccess("Already reported user.");
		}

		int pointsToAdd;
		if (reportRequestDto.getReportKind().equals("광고/사기")) {
			pointsToAdd = 1;
		}else if(reportRequestDto.getReportKind().equals("금지된 표현")){

			pointsToAdd = 2;
		}else if(reportRequestDto.getReportKind().equals("욕설")){

			pointsToAdd = 2;
		}else if(reportRequestDto.getReportKind().equals("음담패설")){

			pointsToAdd = 3;
		}else if(reportRequestDto.getReportKind().equals("노출")){

			pointsToAdd = 5;
		}else if(reportRequestDto.getReportKind().equals("기타")){

			pointsToAdd = 0;
		}else {
			throw new ApiException(REPORT_NOT_REASONABLE);
		}

		reportedUser.addReportPoints(pointsToAdd);
		Report report=new Report(reportedUser,reportRequestDto,user);
		reportRepository.save(report);

		return ResponseDto.setSuccess("Your report has been processed.");
	}
}
