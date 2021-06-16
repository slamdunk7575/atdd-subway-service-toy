package me.toy.atdd.subwayservice.auth.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import me.toy.atdd.subwayservice.AcceptanceTest;
import me.toy.atdd.subwayservice.auth.dto.TokenRequest;
import me.toy.atdd.subwayservice.member.MemberAcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static me.toy.atdd.subwayservice.member.MemberAcceptanceTest.회원_생성을_요청;
import static org.assertj.core.api.Assertions.assertThat;

public class AuthAcceptanceTest extends AcceptanceTest {

    @DisplayName("Bearer Auth")
    @Test
    void myInfoWithBearerAuth() {
        // given
        String email = "wootecam@test.com";
        String password = "1a2b3c";
        int age = 20;
        회원_생성을_요청(email, password, age);

        // when
        ExtractableResponse<Response> 로그인_결과 = 로그인_요청(email, password);

        // then
        로그인_성공함(로그인_결과);
    }

    @DisplayName("Bearer Auth 로그인 실패")
    @Test
    void myInfoWithBadBearerAuth() {
        // when
        ExtractableResponse<Response> 로그인_결과 = 로그인_요청("test@test.com", "4d5e6f");

        // then
        로그인_실패함(로그인_결과);
    }

    @DisplayName("Bearer Auth 유효하지 않은 토큰")
    @Test
    void myInfoWithWrongBearerAuth() {
        // when
        ExtractableResponse<Response> 회원_기능_요청_결과 = 회원_기능_요청("testToken");

        // then
        회원_인증_실패함(회원_기능_요청_결과);
    }

    public static ExtractableResponse<Response> 로그인_요청(String email, String password) {
        TokenRequest tokenRequest = TokenRequest.builder()
                .email(email)
                .password(password)
                .build();

        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(tokenRequest)
                .when().post("/login/token")
                .then().log().all()
                .extract();
    }

    public static void 로그인_성공함(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().jsonPath().getString("accessToken")).isNotNull();
    }

    private void 로그인_실패함(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    private ExtractableResponse<Response> 회원_기능_요청(String testToken) {
        return MemberAcceptanceTest.내정보_조회_요청(testToken);
    }

    private void 회원_인증_실패함(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }
}
