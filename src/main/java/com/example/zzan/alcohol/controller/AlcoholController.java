package com.example.zzan.alcohol.controller;

import com.example.zzan.alcohol.service.AlcoholService;
import com.example.zzan.global.dto.ResponseDto;
import com.example.zzan.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AlcoholController {
    private final AlcoholService alcoholService;

    @PutMapping("/alcohol")
    public ResponseDto<?> updateAlcohol(@RequestBody String email,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return alcoholService.updateAlcohol(email, userDetails.getUser());
    }
}
