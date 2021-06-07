package me.toy.atdd.subwayservice.auth.ui;

import me.toy.atdd.subwayservice.auth.application.AuthService;
import me.toy.atdd.subwayservice.auth.dto.TokenRequest;
import me.toy.atdd.subwayservice.auth.dto.TokenResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login/token")
    public ResponseEntity<TokenResponse> login(@RequestBody TokenRequest tokenRequest) {
        TokenResponse token = authService.login(tokenRequest);
        return ResponseEntity.ok().body(token);
    }

}
