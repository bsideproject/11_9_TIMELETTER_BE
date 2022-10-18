package com.timeletter.api.statistics;

import java.time.LocalDate;

public interface StatisticInterface {
    LocalDate getDate();   // 일자
    String getCount();  // 총 갯수
}