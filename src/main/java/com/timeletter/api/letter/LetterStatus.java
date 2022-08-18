package com.timeletter.api.letter;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LetterStatus {
    DRAFT("임시저장"),
    DONE("작성완료"),
    SUBMIT("전송완료");

    private final String status;
}
