package com.timeletter.api.letter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Letter {

    @Id @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid",strategy = "uuid")
    private String id;

    private String title;                   // 편지 제목
    private String content;                 // 편지 내용
    private LocalDateTime receivedDate;     // 받을 날짜 지정
    private String receivedPhoneNumber;     // 받을 사람 휴대폰 번호 지정

    private String userID;                  // 사용자 ID

    @Enumerated(EnumType.STRING)
    private LetterStatus letterStatus;      // 편지의 상태

    public static Letter toEntity(final LetterDTO dto) {
        return Letter.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .content(dto.getContent())
                .receivedDate(dto.getReceivedDate())
                .receivedPhoneNumber(dto.getReceivedPhoneNumber())
                .userID(dto.getUserID())
                .build();
    }
}
