package com.timeletter.api.member;

import com.timeletter.api.dto.ResponseDTO;
import com.timeletter.api.security.TokenProvider;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@Api(tags = { "Member Info" }, description = "사용자 로그인 및 회원가입 제공")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/member")
public class MemberController {

    private final MemberService memberService;
    private final TokenProvider tokenProvider;

    @Operation(summary = "회원 가입", description = "회원 가입 로직.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK !!"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST !!"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN !!"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND !!"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR !!")
    })
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody MemberDTO memberDTO) {
        try {
            Member member = Member.builder().email(memberDTO.getEmail())
                    .username(memberDTO.getUsername())
                    .password(memberDTO.getPassword())
                    .build();

            Member registeredUser = memberService.create(member);

            MemberDTO responseUserDTO = MemberDTO.builder()
                    .email(registeredUser.getEmail())
                    .username(registeredUser.getUsername())
                    .id(registeredUser.getId()).build();

            return ResponseEntity.ok().body(responseUserDTO);
        } catch (Exception e) {
            ResponseDTO<Object> responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @Operation(summary = "로그인", description = "로그인 로직.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK !!"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST !!"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN !!"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND !!"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR !!")
    })
    @PostMapping("/signin")
    public ResponseEntity<?> authenticate(@RequestBody MemberDTO memberDTO) {
        Member member = memberService.getByCredentials(memberDTO.getEmail(), memberDTO.getPassword());
        if (member != null) {
            final String token = tokenProvider.create(member);
            final MemberDTO responseUserDTO = MemberDTO.builder()
                    .email(member.getEmail())
                    .username(member.getUsername())
                    .id(member.getId())
                    .token(token)
                    .build();
            return ResponseEntity.ok().body(responseUserDTO);
        } else {
            ResponseDTO<Object> responseDTO = ResponseDTO.builder().error("login failed").build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @Operation(summary = "튜토리얼 업데이트", description = "튜토리얼의 설명을 들을것인지 확인해보자")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK !!"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST !!"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN !!"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND !!"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR !!")
    })
    @PutMapping("/tutorial")
    public ResponseEntity<?> updateTutorial(@AuthenticationPrincipal String userId,
            @RequestBody MemberDTO memberDTO) {
        Member member = memberService.findByEmail(userId);
        if (member != null) {
            final Member updateTutorial = memberService.updateTutorial(userId, memberDTO);
            final MemberDTO responseUserDTO = MemberDTO.builder()
                    .email(updateTutorial.getEmail())
                    .tutorialYN(updateTutorial.isTutorialYN())
                    .username(updateTutorial.getUsername())
                    .id(updateTutorial.getId())
                    .build();
            return ResponseEntity.ok().body(responseUserDTO);
        } else {
            ResponseDTO<Object> responseDTO = ResponseDTO.builder().error("login failed").build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }
}
