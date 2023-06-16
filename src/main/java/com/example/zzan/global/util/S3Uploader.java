package com.example.zzan.global.util;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
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
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static com.example.zzan.global.exception.ExceptionEnum.INVALID_FILE;
import static com.example.zzan.global.exception.ExceptionEnum.INVALID_FILE_CONVERSION;

@Slf4j
@RequiredArgsConstructor
@Component
@Service
public class S3Uploader {
	private final AmazonS3Client amazonS3Client;
	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	public String upload(MultipartFile multipartFile, String dirName) throws IOException {
		File uploadFile = convert(multipartFile)
				.orElseThrow(() -> new ApiException(INVALID_FILE_CONVERSION));
		return upload(uploadFile, dirName);
	}

	public String upload(File uploadFile, String dirName) {
		String fileName = dirName + "/" + uploadFile.getName();
		String uploadImageUrl = putS3(uploadFile, fileName);
		removeNewFile(uploadFile);
		return uploadImageUrl;
	}

	public String getRandomImage(String dirName) {
		ListObjectsRequest request = new ListObjectsRequest()
				.withBucketName(bucket)
				.withPrefix(dirName + "/")
				.withDelimiter("/");

		ObjectListing objectListing = amazonS3Client.listObjects(request);
		List<S3ObjectSummary> objectSummaries = objectListing.getObjectSummaries();

		S3ObjectSummary randomSummary = objectSummaries.get(new Random().nextInt(objectSummaries.size()));
		return amazonS3Client.getUrl(bucket, randomSummary.getKey()).toString();
	}

	private String putS3(File uploadFile, String fileName) {
		amazonS3Client.putObject(
				new PutObjectRequest(bucket, fileName, uploadFile)
						.withCannedAcl(CannedAccessControlList.PublicRead)
		);
		return amazonS3Client.getUrl(bucket, fileName).toString();
	}

	public void removeNewFile(File targetFile) {
		if (targetFile.delete()) {
		} else {
		}
	}

	private Optional<File> convert(MultipartFile file) throws IOException {
		String originalFileName = file.getOriginalFilename();
		String ext = originalFileName.substring(originalFileName.lastIndexOf("."));

		if (!ext.equalsIgnoreCase(".png") && !ext.equalsIgnoreCase(".jpg") && !ext.equalsIgnoreCase(".jpeg") && !ext.equalsIgnoreCase(".gif")) {
			throw new ApiException(INVALID_FILE);
		}

		String uuidFileName = UUID.randomUUID().toString().replace("-", "") + ext;

		File convertFile = new File(uuidFileName);
		if (convertFile.createNewFile()) {
			try (FileOutputStream fos = new FileOutputStream(convertFile)) {
				fos.write(file.getBytes());
			}
			return Optional.of(convertFile);
		}
		return Optional.empty();
	}
}