package com.timeletter.api.letter;

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
    private LocalDateTime receivedDate;     // 받을 날짜 지정
    private String receivedPhoneNumber;     // 받을 사람 휴대폰 번호 지정

    public LetterDTO(final Letter letter){
        this.id = letter.getId();
        this.title = letter.getTitle();
        this.content = letter.getContent();
        this.receivedDate = letter.getReceivedDate();
        this.receivedPhoneNumber = letter.getReceivedPhoneNumber();
    }

    public static Letter toEntity(final LetterDTO dto){
        return Letter.builder()
                .id(dto.getId())
                .content(dto.getContent())
                .receivedDate(dto.getReceivedDate())
                .receivedPhoneNumber(dto.getReceivedPhoneNumber())
                .build();
    }
}
