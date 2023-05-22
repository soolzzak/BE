package com.example.zzan.mypage.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

// import com.amazonaws.services.s3.AmazonS3Client;
// import com.amazonaws.services.s3.model.CannedAccessControlList;
// import com.amazonaws.services.s3.model.PutObjectRequest;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor    // final 멤버변수가 있으면 생성자 항목에 포함시킴
@Component
@Service
public class S3Uploader {

	private final AmazonS3Client amazonS3Client;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	//MultipartFile:Spring Framework에서 파일 업로드를 담당하는 인터페이스로 웹 클라이언트가 업로드한
	//파일을 Spring에서 관리하는 형태(웹 클라이언트가 서버로 파일을 업로드할 때, 그 파일 데이터는 MultipartFile 객체에 저장)

	//File:Java에서 파일 시스템의 파일을 추상화한 클래스로 파일의 경로나 이름 등을 다루며, 파일 읽기/쓰기도 가능함
	//MultipartFile은 일시적이며 업로드된 파일을 다른 서비스로 전송해야 하는 경우에는 MultipartFile 객체를 다른 형태로 변환해야 하는데 주로 File객체 즉 java.io.File 객체로 변환

	//디렉토리는 폴더라고 볼수 있으며 dirName는 디렉토리 네임임

	// 클라이언트로 부터 MultipartFile을 전달받아 File로 전환하고 File을 처리하는 upload 메소드를 호출(아래의 convert메소드 이용)
	public String upload(MultipartFile multipartFile, String dirName) throws IOException {
		File uploadFile = convert(multipartFile)//메소드를 호출하여 클라이언트로부터 받은 MultipartFile를 File 객체로 변환
			.orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File 전환 실패"));
		return upload(uploadFile, dirName);//upload() File을 처리하는 upload 메소드를 호출
	}


	//위 메소드로 부터 File을 전달받아 처리(아래 putS3메소드를 이용해 s3에 저장함)
	private String upload(File uploadFile, String dirName) {
		String fileName = dirName + "/" + uploadFile.getName();//업로드할 파일의 이름을 구성
		String uploadImageUrl = putS3(uploadFile, fileName);//putS3() 메소드를 호출하여 File 객체를 S3에 업로드

		removeNewFile(uploadFile);  // 로컬에 생성된 File 삭제 (MultipartFile -> File 전환 하며 로컬에 파일 생성됨)

		return uploadImageUrl;      // 업로드된 파일의 S3 URL 주소 반환(클라이언트에게 전달되어 클라이언트가 업로드된 이미지에 접근할 수 있게됨)
	}


	//주어진 파일을 실질적으로 S3에 업로드
	private String putS3(File uploadFile, String fileName) {
		amazonS3Client.putObject(
			new PutObjectRequest(bucket, fileName, uploadFile)// 업로드 요청에 필요한 매개변수들을 설정
				.withCannedAcl(CannedAccessControlList.PublicRead)	//업로드된 파일에 대한 접근 권한을 설정// PublicRead 권한으로 업로드 됨
		);//실제로 S3에 파일을 업로드
		return amazonS3Client.getUrl(bucket, fileName).toString();//업로드된 파일의 S3 URL을 반환
	}//버킷에는 여러 파일(또는 S3에서는 '객체'라고 부름)을 저장할 수 있음


	//주어진 파일을 로컬 시스템에서 삭제
	private void removeNewFile(File targetFile) {
		if(targetFile.delete()) {
			log.info("파일이 삭제되었습니다.");
		}else {
			log.info("파일이 삭제되지 못했습니다.");
		}
	}


	//MultipartFile 객체를 File 객체로 변환
	private Optional<File> convert(MultipartFile file) throws  IOException {
		File convertFile = new File(file.getOriginalFilename());
		if(convertFile.createNewFile()) {
			try (FileOutputStream fos = new FileOutputStream(convertFile)) {
				fos.write(file.getBytes());
			}
			return Optional.of(convertFile);
		}
		return Optional.empty();
	}

}