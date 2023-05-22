package com.example.zzan.mypage.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class S3Config {
	@Value("${cloud.aws.credentials.access-key}")
	private String accessKey;
	@Value("${cloud.aws.credentials.secret-key}")
	private String secretKey;
	@Value("${cloud.aws.region.static}")
	private String region;

	@Bean
	public AmazonS3Client amazonS3Client() {
		BasicAWSCredentials awsCredentials= new BasicAWSCredentials(accessKey, secretKey);// AWS 서비스에 접근하기 위해 필요한 인증 정보를 생성
		return (AmazonS3Client)AmazonS3ClientBuilder
			.standard()//AmazonS3ClientBuilder의 standard() 메소드를 호출하여 AmazonS3ClientBuilder 객체를 생성
			.withRegion(region)//AWS 서비스를 이용할 특정 지역(region)을 설정
			.withCredentials(new AWSStaticCredentialsProvider(awsCredentials))//위에서 생성한 인증 정보(awsCredentials)를 사용하여 AWS 서비스에 접근하는 데 필요한 자격 증명을 설정
			.build();//위에서 설정한 정보들을 바탕으로 AmazonS3Client 객체를 생성
	}//스프링 빈으로 등록되어, 어플리케이션 내에서 필요한 곳에서 주입받아 사용될 수 있음
}
