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

import static com.example.zzan.global.exception.ExceptionEnum.NOT_ALLOWED_SELF_REPORT;
import static com.example.zzan.global.exception.ExceptionEnum.TARGET_USER_NOT_FOUND;

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
		if (reportRequestDto.getReportKind().equals("Advertisement/Scam")) {
			pointsToAdd = 1;
		}else if(reportRequestDto.getReportKind().equals("Forbidden Expression")){

			pointsToAdd = 2;
		}else if(reportRequestDto.getReportKind().equals("Abusive Language")){

			pointsToAdd = 2;
		}else if(reportRequestDto.getReportKind().equals("Indecent Remark")){

			pointsToAdd = 3;
		}else if(reportRequestDto.getReportKind().equals("Exposure")){

			pointsToAdd = 5;
		}else if(reportRequestDto.getReportKind().equals("Others")){

			pointsToAdd = 0;
		}else {
			return ResponseDto.setBadRequest("The reason for reporting is not appropriate.");
		}

		reportedUser.addReportPoints(pointsToAdd);
		Report report=new Report(reportedUser,reportRequestDto,user);
		reportRepository.save(report);

		return ResponseDto.setSuccess("Your report has been processed.");
	}
}
