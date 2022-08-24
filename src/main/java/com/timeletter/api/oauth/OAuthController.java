package com.timeletter.api.oauth;

import com.timeletter.api.dto.ResponseDTO;
import com.timeletter.api.member.Member;
import com.timeletter.api.member.MemberDTO;
import com.timeletter.api.member.MemberService;
import com.timeletter.api.security.TokenProvider;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Api(tags = {"Kakao Login"}, description = "카카오 로그인")
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
    @Operation(summary = "카카오 로그인", description = "코드 기반의 카카오 로그인 로직.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK !!"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST !!"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN !!"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND !!"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR !!")
    })
    @ResponseBody
    @GetMapping("/kakao")
    public ResponseEntity<?> kakaoCallback(@RequestParam String code) {
        try {
            String kakaoAccessToken = oAuthService.getKakaoAccessToken(code);
            log.info("access_token : " + kakaoAccessToken);
            final MemberDTO responseUserDTO = MemberDTO.builder()
                    .token(kakaoAccessToken)
                    .build();
            return ResponseEntity.ok().body(responseUserDTO);

        }catch (Exception e){
            e.printStackTrace();
            ResponseDTO<Object> responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }
    /**
     * 카카오 callback
     * [GET] /oauth/kakao/callback
     */
    @Operation(summary = "카카오 로그인", description = "코드 기반의 카카오 로그인 로직.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK !!"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST !!"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN !!"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND !!"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR !!")
    })
    @ResponseBody
    @GetMapping("/accessToken")
    public ResponseEntity<?> kakaoAccessToken(@RequestParam String token) {
        try {
            log.info("access_token : " + token);
            Member member = oAuthService.createKakaoUser(token);

            // 백엔드 서버에 해당 정보가 존재하는지 확인
            if(!memberService.existByEmail(member.getEmail())){
                log.info("kakao 회원가입 로직 시작");
                Member newKakaoUser = Member.builder().email(member.getEmail()).password(member.getId()).username(member.getNickname()).build();
                memberService.create(newKakaoUser);
                log.info("kakao 회원가입 로직 완료");
            }
            Member byEmailAndPassword = memberService.findByEmail(member.getEmail());
            final String loginToken = tokenProvider.create(byEmailAndPassword);
            final MemberDTO responseUserDTO = MemberDTO.builder()
                    .email(member.getEmail())
                    .id(member.getId())
                    .token(loginToken)
                    .build();
            return ResponseEntity.ok().body(responseUserDTO);

        }catch (Exception e){
            e.printStackTrace();
            ResponseDTO<Object> responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }
}