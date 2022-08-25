package com.timeletter.api.letter;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LetterStatus {
    DRAFT("임시저장"),
    DONE("작성완료"),
    SUBMIT("전송완료"),
    NOT_YET("아직 시간이 도래하지 않음");

    private final String status;
}
