package com.example.zzan.user.service;

import com.example.zzan.global.exception.ApiException;
import com.example.zzan.user.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Random;

import static com.example.zzan.global.exception.ExceptionEnum.ALREADY_SIGNUP_EMAIL;

@PropertySource("classpath:application.yml")
@Slf4j
@RequiredArgsConstructor
@Service
public class MailService {
    private final JavaMailSender javaMailSender;
    private final UserRepository userRepository;
    private final String ePw = createKey();
    @Value("${spring.mail.username}")
    private String id;

    public MimeMessage createMessage(String to, String code) throws MessagingException, UnsupportedEncodingException {
        log.info("보내는 대상 : " + to);
        log.info("인증 번호 : " + code);
        MimeMessage message = javaMailSender.createMimeMessage();

        message.addRecipients(MimeMessage.RecipientType.TO, to);
        message.setSubject("honsoolzzak 회원가입 이메일 인증");

        String msg = "";
        msg += "<div style='margin:20px;'>";
        msg += "<h1> 안녕하세요 < honsoolzzak > 입니다. </h1>";
        msg += "<br>";
        msg += "<p>아래 코드를 복사해 입력해주세요<p>";
        msg += "<br>";
        msg += "<p>감사합니다.<p>";
        msg += "<br>";
        msg += "<div style=' font-family:verdana';>";
        msg += "<h3 style='color:blue;'>회원가입 인증 코드입니다.</h3>";
        msg += "<div style='font-size:130%'>";
        msg += "CODE : <strong>";
        msg += code + "</strong><div><br/> ";
        msg += "</div>";

        message.setText(msg, "utf-8", "html");
        message.setFrom(new InternetAddress(id, "honsoolzzak.com"));

        return message;
    }

    public static String createKey() {
        StringBuffer key = new StringBuffer();
        Random rnd = new Random();

        for (int i = 0; i < 6; i++) {
            key.append((rnd.nextInt(10)));
        }
        return key.toString();
    }

    public String sendSimpleMessage(String to) throws Exception {
        boolean emailExists = userRepository.existsByEmail(to);
        if (emailExists) {
            throw new ApiException(ALREADY_SIGNUP_EMAIL);
        }

        String code = createKey();
        MimeMessage message = createMessage(to, code);
        try {
            javaMailSender.send(message);
        } catch (MailException es) {
            es.printStackTrace();
            throw new IllegalArgumentException();
        }
        return code;
    }
}
