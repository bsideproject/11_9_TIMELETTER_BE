package com.timeletter.api.reminder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReminderDTO {
    private String reminderId;
    private String letterId;
    private String urlSlug;
    private String userId;
    private String senderName;
    private String recipientName;
    private LocalDateTime sendDate;
    private LocalDateTime receiveDate;
    private String recipientPhoneNumber;
}
