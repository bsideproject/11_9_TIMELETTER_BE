package com.timeletter.api.letter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Letter {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "letter_id")
    private String id;

    private String urlSlug;

    // private String title; // 편지 제목
    private String content; // 편지 내용
    private LocalDateTime receivedDate; // 받을 날짜 지정
    // private String receivedPhoneNumber; // 받을 사람 휴대폰 번호 지정

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt; // 생성 시간 지정

    private String userID; // 사용자 ID

    @Enumerated(EnumType.STRING)
    private LetterStatus letterStatus; // 편지의 상태

    private String senderName; // 보내는 사람의 이름

    private String receiverName; // 받는 사람의 이름

    public static Letter toEntity(final LetterDTO dto) {
        return Letter.builder()
                .id(dto.getId())
                // .title(dto.getTitle())
                .urlSlug(dto.getUrlSlug())
                .content(dto.getContent())
                .receivedDate(dto.getReceivedDate())
                // .receivedPhoneNumber(dto.getReceivedPhoneNumber())
                .createdAt(LocalDateTime.now())
                .userID(dto.getUserID())
                .letterStatus(LetterStatus.DRAFT)
                .senderName(dto.getSenderName())
                .receiverName(dto.getReceiverName())
                .build();
    }
}
