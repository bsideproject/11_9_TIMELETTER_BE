package com.timeletter.api.status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class StatusDTO {
    private long memberCount;
    private long letterCount;
    private long reminderCount;

}
