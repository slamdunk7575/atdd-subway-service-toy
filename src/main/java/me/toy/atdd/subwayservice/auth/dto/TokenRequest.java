package me.toy.atdd.subwayservice.auth.dto;

import lombok.Builder;

public class TokenRequest {

    private String email;
    private String password;

    public TokenRequest() {
    }

    @Builder
    public TokenRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
