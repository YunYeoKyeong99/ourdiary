package com.flower.ourdiary.controller;

import com.flower.ourdiary.service.EmailAuthService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.net.URI;

@ApiIgnore
@RestController
@RequestMapping("/email/auth")
@RequiredArgsConstructor
public class EmailAuthController {

    private final EmailAuthService emailAuthService;

    @ApiOperation(value = "", hidden = true)
    @GetMapping("/{token}")
    public ResponseEntity<Void> authEmail(@PathVariable String token) throws Exception {
        HttpHeaders httpHeaders = new HttpHeaders();

        switch (emailAuthService.authEmail(token)) {
            case SUCC:
                httpHeaders.setLocation(new URI("/email-auth-valid.html"));
                break;
            case ALREADY_SUCC:
                httpHeaders.setLocation(new URI("/email-auth-already-valid.html"));
                break;
            default:
                httpHeaders.setLocation(new URI("/email-auth-invalid.html"));
                break;
        }

        return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
    }

}
