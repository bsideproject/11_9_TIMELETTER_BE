package com.timeletter.api.reminder;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReminderRepository extends JpaRepository<Reminder, String> {

    Boolean existsByLetterIdAndUserId(String letterId, String userId);

}
