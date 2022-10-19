package com.timeletter.api.member;

import com.timeletter.api.statistics.StatisticInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Member create(final Member member) {

        if (member == null || member.getEmail() == null) {
            throw new RuntimeException("Invalid arguments");
        }
        final String email = member.getEmail();
        if (memberRepository.existsByEmail(email)) {
            log.warn("Eamil already exists {}", email);
            throw new RuntimeException("Email already exists");
        }

        member.setPassword(passwordEncoder.encode(member.getPassword()));
        member.setTutorialYN(true); // 튜토리얼을 참으로 한다.

        return memberRepository.save(member);
    }

    public Member getByCredentials(final String email, final String password) {
        Member byEmail = memberRepository.findByEmail(email);
        if (!passwordEncoder.matches(password, byEmail.getPassword())) {
            log.warn("password not matched {}", email);
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

    /**
     * 편지내용을 업데이트 한다.
     *
     *
     * @param userId
     * @param memberDTO 사용자 DTO
     * @return save 수정된 사용자
     */
    public Member updateTutorial(String userId, final MemberDTO memberDTO) {
        Member byEmail = memberRepository.findByEmail(userId);
        byEmail.setTutorialYN(memberDTO.isTutorialYN());
        Member save = memberRepository.save(byEmail);

        return save;
    }

    public Long getMemberCount() {
        return memberRepository.count();
    }

    public List<StatisticInterface> memCountGroupByDate(String stDate, String edDate) {
        LocalDate startDate = stDate.isEmpty() ? LocalDate.now().minusWeeks(1) : strToLocalDateTime(stDate);
        LocalDate endDate = edDate.isEmpty() ? LocalDate.now() : strToLocalDateTime(edDate);

        log.info("조회 시작 일 : " + startDate);
        log.info("조회 종료 일 : " + endDate);

        return memberRepository.findGroupByRegDate(startDate, endDate);
    }

    private LocalDate strToLocalDateTime(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd"));
    }
}
