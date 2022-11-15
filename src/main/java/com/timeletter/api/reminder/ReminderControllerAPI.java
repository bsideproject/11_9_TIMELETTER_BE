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
        log.info("리마인더 API에 도달하였습니다.{}", letterId);

        Member member = memberService.findByEmail(userId);
        Optional<Letter> letter = letterService.retrieve(letterId);
        if (letter.isPresent()) {
            Reminder reminder = Reminder.builder().letterId(letterId).userId(userId)
                    .senderName(letter.get().getSenderName())
                    .sendDate(letter.get().getCreatedAt())
                    .receiveDate(letter.get().getReceivedDate())
                    .urlSlug(letter.get().getUrlSlug())
                    .recipientName(member.getUsername())
                    .recipientPhoneNumber(member.getPhoneNumber())
                    .build();

            // letterId 와 userId를 넣어서, 중복 신청인지 확인후, 중복이라면 error

            if (reminderService.isSendedValidate(letterId, userId)) {
                Reminder returnReminder = reminderService.create(reminder);
                reminderService.sendReminderComplated(reminder);
                reminderService.sendReminder(reminder);
                ReminderResponceDTO response = ReminderResponceDTO.builder().isSended(true).build();
                return ResponseEntity.ok().body(response);
            } else {
                ReminderResponceDTO response = ReminderResponceDTO.builder().isSended(false).build();
                // ResponseDTO<Object> responseDTO = ResponseDTO.builder().error("reminder is
                // duplicate").data().build();
                return ResponseEntity.badRequest().body(response);
            }
        } else {
            ResponseDTO<Object> responseDTO = ResponseDTO.builder().error("reminder create fail").build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

}
