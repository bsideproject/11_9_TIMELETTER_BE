package com.timeletter.api.status;

import com.timeletter.api.dto.ResponseDTO;
import com.timeletter.api.dto.PageRequestDTO;
import com.timeletter.api.image.ImageService;
import com.timeletter.api.letter.Letter;
import com.timeletter.api.letter.LetterService;
import com.timeletter.api.member.Member;
import com.timeletter.api.member.MemberService;
import com.timeletter.api.reminder.ReminderService;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Slf4j
@CrossOrigin("*")
@Api(tags = { "Status Info" }, description = "상태 관련 API")
@RestController
@AllArgsConstructor
@RequestMapping("/v1/status")
public class StatusControllerAPI {
    private final ReminderService reminderService;
    private final LetterService letterService;
    private final MemberService memberService;

    @Operation(summary = "전체 상태 확인", description = "전체 상태를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK !!"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST !!"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN !!"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND !!"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR !!")
    })
    @GetMapping
    public ResponseEntity<?> modifyLetterStatus() {

        StatusDTO responseUserDTO = StatusDTO.builder()
                .memberCount(memberService.getMemberCount())
                .letterCount(letterService.getLetterCount())
                .reminderCount(reminderService.getReminderCount()).build();

        return ResponseEntity.ok().body(responseUserDTO);
    }
}
