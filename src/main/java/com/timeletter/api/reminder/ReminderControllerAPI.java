package com.timeletter.api.reminder;

import com.timeletter.api.dto.ResponseDTO;
import com.timeletter.api.dto.PageRequestDTO;
import com.timeletter.api.image.ImageService;
import com.timeletter.api.letter.Letter;
import com.timeletter.api.letter.LetterService;
import com.timeletter.api.member.Member;
import com.timeletter.api.member.MemberService;

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
@Api(tags = { "Reminder Info" }, description = "리마인더 관련 API")
@RestController
@AllArgsConstructor
@RequestMapping("/v1/reminder")
public class ReminderControllerAPI {
    private final ReminderService reminderService;
    private final LetterService letterService;
    private final MemberService memberService;

    @Operation(summary = "리마인더 생성", description = "리마인더 생성.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK !!"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST !!"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN !!"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND !!"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR !!")
    })
    @PostMapping
    public ResponseEntity<?> create(@RequestParam("letterId") String letterId,
            @AuthenticationPrincipal String userId) {

        Member member = memberService.findByEmail(userId);
        Optional<Letter> letter = letterService.retrieve(letterId);
        if (letter.isPresent()) {
            Reminder reminder = Reminder.builder().letterId(letterId).userId(userId)
                    .senderName(letter.get().getSenderName())
                    .sendDate(letter.get().getReceivedDate())
                    .urlSlug(letter.get().getUrlSlug())
                    .receivedPhoneNumber(member.getPhoneNumber())
                    .build();
            // sentDate 에 넣을 letter created 필요함

            Reminder returnReminder = reminderService.create(reminder);
            return ResponseEntity.ok().body(returnReminder);
        } else {
            ResponseDTO<Object> responseDTO = ResponseDTO.builder().error("reminder create fail").build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }
}
