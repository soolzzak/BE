package com.example.zzan.mail.controller;

import com.example.zzan.mail.dto.MailRequestDto;
import com.example.zzan.mail.dto.MailResponseDto;
import com.example.zzan.mail.service.MailService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "MailController", description = "이메일 인증 파트")
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MailController {
    private final MailService mailService;

    @PostMapping("/signup/mailconfirm")
    public MailResponseDto mailConfirm(@RequestBody MailRequestDto mailRequestDto) throws Exception {
        String code = mailService.sendSimpleMessage(mailRequestDto.getEmail());
        return new MailResponseDto(code);
    }

    @PostMapping("/login/mailconfirm")
    public MailResponseDto mailConfirm2(@RequestBody MailRequestDto mailRequestDto) throws Exception {
        String code = mailService.sendSimpleMessage2(mailRequestDto.getEmail());
        return new MailResponseDto(code);
    }
}
