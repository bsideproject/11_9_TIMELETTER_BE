package com.timeletter.api.member;

import com.timeletter.api.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/member")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody MemberDTO memberDTO){
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
        }catch (Exception e){

            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();

            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticate(@RequestBody MemberDTO memberDTO){
        Member member = memberService.getByCredentials(memberDTO.getEmail(), memberDTO.getPassword());

        if(member != null){
            final MemberDTO responseUserDTO = MemberDTO.builder()
                    .email(member.getUsername())
                    .id(member.getId()).build();

            return ResponseEntity.ok().body(responseUserDTO);
        }else{
            ResponseDTO responseDTO = ResponseDTO.builder().error("login failed").build();

            return ResponseEntity.badRequest().body(responseDTO);
        }
    }
}
