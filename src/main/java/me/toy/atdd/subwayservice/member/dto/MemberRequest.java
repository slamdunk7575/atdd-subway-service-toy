package me.toy.atdd.subwayservice.member.dto;

import lombok.Builder;
import me.toy.atdd.subwayservice.member.domain.Member;

public class MemberRequest {
    private String email;
    private String password;
    private Integer age;

    public MemberRequest() {
    }

    @Builder
    public MemberRequest(String email, String password, Integer age) {
        this.email = email;
        this.password = password;
        this.age = age;
    }

    public Member toMember() {
        return new Member(email, password, age);
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Integer getAge() {
        return age;
    }

}
