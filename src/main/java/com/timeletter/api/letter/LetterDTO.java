package com.timeletter.api.letter;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LetterDTO {
    private String id;
    // private String title;                   // 편지 제목
    private String content;                 // 편지 내용

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime receivedDate;     // 받을 날짜 지정
    //private String receivedPhoneNumber;     // 받을 사람 휴대폰 번호 지정

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;               // 생성 시간 지정

    private String userID;                  // 사용자 ID - 누가 작성했는지 알려준다.

    private LetterStatus letterStatus;

    private String imageId;                 // 등록된 이미지 아이디

    private String senderName;              // 보내는 사람의 이름

    private String receiverName;            // 받는 사람의 이름

    public LetterDTO(final Letter letter){
        this.id = letter.getId();
        //this.title = letter.getTitle();
        this.content = letter.getContent();
        this.receivedDate = letter.getReceivedDate();
        //this.receivedPhoneNumber = letter.getReceivedPhoneNumber();
        this.createdAt = letter.getCreatedAt();
        this.userID = letter.getUserID();
        this.letterStatus = letter.getLetterStatus();
        this.senderName = letter.getSenderName();
        this.receiverName = letter.getReceiverName();
    }

    public static Letter toEntity(final LetterDTO dto){
        return Letter.builder()
                .id(dto.getId())
                //.title(dto.getTitle())
                .content(dto.getContent())
                .receivedDate(dto.getReceivedDate())
                //.receivedPhoneNumber(dto.getReceivedPhoneNumber())
                .createdAt(dto.getCreatedAt())
                .userID(dto.getUserID())
                .letterStatus(dto.getLetterStatus())
                .senderName(dto.getSenderName())
                .receiverName(dto.getReceiverName())
                .build();
    }
}
