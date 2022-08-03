package com.timeletter.api.oauth;

import com.timeletter.api.dto.ResponseDTO;
import com.timeletter.api.member.Member;
import com.timeletter.api.member.MemberDTO;
import com.timeletter.api.member.MemberService;
import com.timeletter.api.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth")
public class OAuthController {

    private final OAuthService oAuthService;
    private final MemberService memberService;
    private final TokenProvider tokenProvider;

    /**
     * 카카오 callback
     * [GET] /oauth/kakao/callback
     */
    @ResponseBody
    @GetMapping("/kakao")
    public ResponseEntity<?> kakaoCallback(@RequestParam String code) {
        try {
            String kakaoAccessToken = oAuthService.getKakaoAccessToken(code);
            log.info("access_token : " + kakaoAccessToken);
            Member member = oAuthService.createKakaoUser(kakaoAccessToken);

            // 백엔드 서버에 해당 정보가 존재하는지 확인
            if(!memberService.existByEmail(member.getEmail())){
                log.info("kakao 회원가입 로직 시작");
                Member newKakaoUser = Member.builder().email(member.getEmail()).password(member.getId()).username(member.getUsername()).build();
                memberService.create(newKakaoUser);
                log.info("kakao 회원가입 로직 완료");
            }
            Member byEmailAndPassword = memberService.findByEmail(member.getEmail());
            final String token = tokenProvider.create(byEmailAndPassword);
            final MemberDTO responseUserDTO = MemberDTO.builder()
                    .email(member.getEmail())
                    .id(member.getId())
                    .token(token)
                    .build();
            return ResponseEntity.ok().body(responseUserDTO);

        }catch (Exception e){
            e.printStackTrace();
            ResponseDTO<Object> responseDTO = ResponseDTO.builder().error("카카오 회원가입 및 로그인 도중 에러가 발생했습니다.").build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }
}