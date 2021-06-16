package me.toy.atdd.subwayservice.auth.application;

import me.toy.atdd.subwayservice.auth.domain.LoginMember;
import me.toy.atdd.subwayservice.auth.dto.TokenRequest;
import me.toy.atdd.subwayservice.auth.dto.TokenResponse;
import me.toy.atdd.subwayservice.auth.infrastructure.JwtTokenProvider;
import me.toy.atdd.subwayservice.member.domain.Member;
import me.toy.atdd.subwayservice.member.domain.MemberRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private MemberRepository memberRepository;
    private JwtTokenProvider jwtTokenProvider;

    public AuthService(MemberRepository memberRepository, JwtTokenProvider jwtTokenProvider) {
        this.memberRepository = memberRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public TokenResponse login(TokenRequest tokenRequest) {
        Member member = getMember(tokenRequest.getEmail());
        member.checkPassword(tokenRequest.getPassword());
        String token = jwtTokenProvider.createToken(tokenRequest.getEmail());
        return new TokenResponse(token);
    }

    private Member getMember(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(AuthorizationException::new);
    }

    public LoginMember findMemberByToken(String credentials) {
        if (jwtTokenProvider.validateToken(credentials)) {
            return new LoginMember();
        }

        String email = jwtTokenProvider.getPayLoad(credentials);
        Member member = getMember(email);

        return new LoginMember(member.getId(), member.getEmail(), member.getAge());
    }

}
