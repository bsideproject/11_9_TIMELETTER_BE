package com.timeletter.api.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Member create(final Member member){

        if(member == null || member.getEmail() == null){
            throw new RuntimeException("Invalid arguments");
        }
        final String email = member.getEmail();
        if(memberRepository.existsByEmail(email)){
            log.warn("Eamil already exists {}",email);
            throw new RuntimeException("Email already exists");
        }

        return memberRepository.save(member);
    }

    public Member getByCredentials(final String email,final String password){
        return memberRepository.findByEmailAndPassword(email,password);
    }
}
