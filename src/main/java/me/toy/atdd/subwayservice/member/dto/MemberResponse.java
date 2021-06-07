package me.toy.atdd.subwayservice.member.dto;

import me.toy.atdd.subwayservice.member.domain.Member;

public class MemberResponse {
    private long id;
    private String email;
    private int age;

    public MemberResponse() {
    }

    public MemberResponse(long id, String email, int age) {
        this.id = id;
        this.email = email;
        this.age = age;
    }

    public static MemberResponse of(Member member) {
        return new MemberResponse(member.getId(), member.getEmail(), member.getAge());
    }

    public long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public int getAge() {
        return age;
    }
}
