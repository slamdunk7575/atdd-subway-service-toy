package me.toy.atdd.subwayservice.member.application;

import me.toy.atdd.subwayservice.member.domain.Member;
import me.toy.atdd.subwayservice.member.domain.MemberRepository;
import me.toy.atdd.subwayservice.member.dto.MemberRequest;
import me.toy.atdd.subwayservice.member.dto.MemberResponse;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public MemberResponse createMember(MemberRequest memberRequest) {
        Member member = memberRepository.save(memberRequest.toMember());
        return MemberResponse.of(member);
    }

    public MemberResponse findMember(Long id) {
        Member member = memberRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return MemberResponse.of(member);
    }

    public void updateMember(Long id, MemberRequest memberRequest) {
        Member member = memberRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        member.update(memberRequest.toMember());
    }

    public void deleteMember(Long id) {
        memberRepository.deleteById(id);
    }
}
