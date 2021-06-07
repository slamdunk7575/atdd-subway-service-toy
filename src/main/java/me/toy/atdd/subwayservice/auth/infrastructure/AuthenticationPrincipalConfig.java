package me.toy.atdd.subwayservice.auth.infrastructure;

import me.toy.atdd.subwayservice.auth.application.AuthService;
import me.toy.atdd.subwayservice.auth.ui.AuthenticationPrincipalArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class AuthenticationPrincipalConfig implements WebMvcConfigurer {

    private final AuthService authService;

    public AuthenticationPrincipalConfig(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void addArgumentResolvers(List argumentResolvers) {
        argumentResolvers.add(createAuthenticationPrincipalArgumentResolver());
    }

    public AuthenticationPrincipalArgumentResolver createAuthenticationPrincipalArgumentResolver() {
        return new AuthenticationPrincipalArgumentResolver(authService);
    }
}
