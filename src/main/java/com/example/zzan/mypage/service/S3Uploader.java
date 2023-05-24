package com.example.zzan.mypage.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.zzan.global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static com.example.zzan.global.exception.ExceptionEnum.INVALID_FILE;

@Slf4j
@RequiredArgsConstructor    // final 멤버변수가 있으면 생성자 항목에 포함시킴
@Component
@Service
public class S3Uploader {

	private final AmazonS3Client amazonS3Client;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	public String upload(MultipartFile multipartFile, String dirName) throws IOException {
		File uploadFile = convert(multipartFile)
			.orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File 전환 실패"));
		return upload(uploadFile, dirName);
	}

	private String upload(File uploadFile, String dirName) {
		String fileName = dirName + "/" + uploadFile.getName();
		String uploadImageUrl = putS3(uploadFile, fileName);

		removeNewFile(uploadFile);

		return uploadImageUrl;
	}

	private String putS3(File uploadFile, String fileName) {
		amazonS3Client.putObject(
			new PutObjectRequest(bucket, fileName, uploadFile)
				.withCannedAcl(CannedAccessControlList.PublicRead)
		);
		return amazonS3Client.getUrl(bucket, fileName).toString();
	}

	public void removeNewFile(File targetFile) {
		if(targetFile.delete()) {
			log.info("파일이 삭제되었습니다.");
		}else {
			log.info("파일이 삭제되지 못했습니다.");
		}
	}

	private Optional<File> convert(MultipartFile file) throws  IOException {

		// 확장자 가져오기//substring 메소드를 사용하여 원래 파일 이름에서 마지막에 나타나는 점 (.) 이후의 모든 문자열을 가져옴
		String originalFileName = file.getOriginalFilename();
		String ext = originalFileName.substring(originalFileName.lastIndexOf("."));


		// PNG 파일만 허용
		if (!ext.equalsIgnoreCase(".png")) {
			throw new ApiException(INVALID_FILE);
		}

		// UUID를 이용한 새로운 파일 이름 생성// UUID를 생성하고, UUID 문자열에서 "-"를 제거한 후, 파일 확장자를 덧붙여 새로운 파일 이름을 만듦
		String uuidFileName = UUID.randomUUID().toString().replace("-", "") + ext;


		File convertFile = new File(uuidFileName);
		if(convertFile.createNewFile()) {
			try (FileOutputStream fos = new FileOutputStream(convertFile)) {
				fos.write(file.getBytes());
			}
			return Optional.of(convertFile);
		}
		return Optional.empty();
	}
}