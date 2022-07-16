package com.timeletter.api.letter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LetterTest {

    @Test
    @DisplayName("letter 객체가 잘 생성되는지 테스트")
    public void createTest(){
        try {
            Letter letter = new Letter();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
