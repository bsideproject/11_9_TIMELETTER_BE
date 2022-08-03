package com.timeletter.api.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Member create(final Member member){

        if(member == null || member.getEmail() == null){
            throw new RuntimeException("Invalid arguments");
        }
        final String email = member.getEmail();
        if(memberRepository.existsByEmail(email)){
            log.warn("Eamil already exists {}",email);
            throw new RuntimeException("Email already exists");
        }

        member.setPassword(passwordEncoder.encode(member.getPassword()));

        return memberRepository.save(member);
    }

    public Member getByCredentials(final String email,final String password){
        Member byEmail = memberRepository.findByEmail(email);
        if(!passwordEncoder.matches(password, byEmail.getPassword())){
            log.warn("password not matched {}",email);
            throw new RuntimeException("password not matched");
        }

        return byEmail;
    }

    public boolean existByEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }
}
