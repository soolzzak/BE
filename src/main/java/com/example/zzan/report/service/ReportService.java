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

import static com.example.zzan.global.exception.ExceptionEnum.TARGET_USER_NOT_FOUND;
import static com.example.zzan.global.exception.ExceptionEnum.USER_CANNOT_REPORT_SELF;

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
			throw new ApiException(USER_CANNOT_REPORT_SELF);
		}

		Optional<Report> existingReport = reportRepository.findByReportingUserAndReportedUser(user,reportedUser);
		if (existingReport.isPresent()) {
			// This user has already reported the same user.
			return ResponseDto.setSuccess("이미 신고한 유저입니다");
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
			return ResponseDto.setBadRequest("신고사유가 적절치 않습니다");
		}

		reportedUser.addReportPoints(pointsToAdd);
		Report report=new Report(reportedUser,reportRequestDto,user);
		reportRepository.save(report);

		return ResponseDto.setSuccess("신고가 처리 되었습니다");
	}
}
