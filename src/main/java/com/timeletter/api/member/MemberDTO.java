package com.timeletter.api.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {
    private String token;
    private String email;
    private String username;
    private String password;
    private String id;
    private String nickname;        // 카카오 프로필 닉네임
    private String gender;
    private String ageRange;        // 연령대
    private String birthday;
    private String birthyear;       // 출생연도
    private String phoneNumber;
    private boolean tutorialYN;     // 튜토리얼을 진행할 것인지 확인
    private LocalDate regDate;
}
