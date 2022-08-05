package com.timeletter.api.letter;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LetterDTO {
    private String id;
    private String title;                   // 편지 제목
    private String content;                 // 편지 내용

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime receivedDate;     // 받을 날짜 지정
    private String receivedPhoneNumber;     // 받을 사람 휴대폰 번호 지정

    private String userID;                  // 사용자 ID - 누가 작성했는지 알려준다.

    private LetterStatus letterStatus;

    public LetterDTO(final Letter letter){
        this.id = letter.getId();
        this.title = letter.getTitle();
        this.content = letter.getContent();
        this.receivedDate = letter.getReceivedDate();
        this.receivedPhoneNumber = letter.getReceivedPhoneNumber();
        this.userID = letter.getUserID();
        this.letterStatus = letter.getLetterStatus();
    }

    public static Letter toEntity(final LetterDTO dto){
        return Letter.builder()
                .id(dto.getId())
                .content(dto.getContent())
                .receivedDate(dto.getReceivedDate())
                .receivedPhoneNumber(dto.getReceivedPhoneNumber())
                .userID(dto.getUserID())
                .letterStatus(dto.getLetterStatus())
                .build();
    }
}
