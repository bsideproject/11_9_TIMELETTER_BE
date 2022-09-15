package com.timeletter.api.reminder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.timeletter.api.letter.Letter;
import com.timeletter.api.letter.LetterRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ReminderService {

    private final ReminderRepository reminderRepository;

    @Transactional
    public Reminder create(Reminder reminder) {
        validate(reminder);
        if (reminderRepository.existByLetterIdAndUserId(reminder.getLetterId(), reminder.getUserId())) {
            log.warn("User already applied for a reminder {}", reminder.getUserId());
            throw new RuntimeException("User already applied for a reminder");
        }
        Reminder save = save(reminder);
        return save;
    }

    private void validate(Reminder entity) {
        if (entity == null) {
            log.warn("Entity cannot be null.");
            throw new RuntimeException("Entity cannot be null");
        }
    }

    @Transactional
    public Reminder save(Reminder reminder) {
        return reminderRepository.save(reminder);
    }

}
