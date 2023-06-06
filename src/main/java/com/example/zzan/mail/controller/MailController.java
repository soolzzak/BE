package com.example.zzan.mail.controller;

import com.example.zzan.mail.dto.MailRequestDto;
import com.example.zzan.mail.dto.MailResponseDto;
import com.example.zzan.mail.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
}
