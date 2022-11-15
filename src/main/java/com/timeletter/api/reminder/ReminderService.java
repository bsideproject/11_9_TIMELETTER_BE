package com.timeletter.api.reminder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
import net.nurigo.sdk.message.model.KakaoOption;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ReminderService {
    @Value("${solapi.api-key}")
    private String API_KEY;
    @Value("${solapi.secret-key}")
    private String SECRET_KEY;
    @Value("${solapi.template-id}")
    private String TEMPLATE_ID;
    @Value("${solapi.template-completed-id}")
    private String TEMPLATE_COMPLETED_ID;
    @Value("${solapi.pfid}")
    private String PFID;
    @Value("${solapi.send-phone-number}")
    private String SEND_PHONE_NUMBER;

    private final ReminderRepository reminderRepository;

    public Reminder create(Reminder reminder) {
        validate(reminder);
        if (reminderRepository.existsByLetterIdAndUserId(reminder.getLetterId(), reminder.getUserId())) {
            log.warn("User already applied for a reminder {}", reminder.getUserId());
            throw new RuntimeException("User already applied for a reminder");
        }
        Reminder save = save(reminder);
        return save;
    }

    public boolean isSendedValidate(String letterId, String userId) {
        if (reminderRepository.existsByLetterIdAndUserId(letterId, userId)) {
            log.warn("User already applied for a reminder {}", userId);
            // throw new RuntimeException("User already applied for a reminder");
            return false;
        }
        return true;
    }

    public boolean sendReminderComplated(Reminder reminder) {
        DefaultMessageService messageService = NurigoApp.INSTANCE.initialize(API_KEY, SECRET_KEY,
                "https://api.solapi.com");

        String[] splitNumer = reminder.getRecipientPhoneNumber().split("-");
        // CharSequence cs = reminder.getReceiveDate().toString();
        // LocalDateTime letterOpendate = LocalDateTime.parse(cs,
        // DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        KakaoOption kakaoOption = new KakaoOption();
        kakaoOption.setPfId(PFID);
        kakaoOption.setTemplateId(TEMPLATE_COMPLETED_ID);

        HashMap<String, String> variables = new HashMap<>();
        variables.put("#{send_name}", reminder.getSenderName());
        variables.put("#{letter_opendate}", reminder.getReceiveDate().toString());
        variables.put("#{letter_url}", reminder.getUrlSlug().toString());
        kakaoOption.setVariables(variables);

        Message message = new Message();
        message.setFrom(SEND_PHONE_NUMBER);
        message.setTo("010" + splitNumer[1] + splitNumer[2]);
        message.setKakaoOptions(kakaoOption);

        try {
            // send 메소드로 ArrayList<Message> 객체를 넣어도 동작합니다!
            messageService.send(message);
        } catch (NurigoMessageNotReceivedException exception) {
            // 발송에 실패한 메시지 목록을 확인할 수 있습니다!
            System.out.println(exception.getFailedMessageList());
            System.out.println(exception.getMessage());
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }

        return true;
    }

    public boolean sendReminder(Reminder reminder) {
        DefaultMessageService messageService = NurigoApp.INSTANCE.initialize(API_KEY, SECRET_KEY,
                "https://api.solapi.com");

        String[] splitNumer = reminder.getRecipientPhoneNumber().split("-");

        log.info("API_KEY {}", API_KEY);
        log.info("SECRET_KEY {}", SECRET_KEY);
        log.info("TEMPLATE_ID {}", TEMPLATE_ID);
        log.info("TEMPLATE_COMPLETED_ID {}", TEMPLATE_COMPLETED_ID);
        log.info("PFID {}", PFID);
        log.info("SEND_PHONE_NUMBER {}", SEND_PHONE_NUMBER);
        log.info("splitNumer {}", "010" + splitNumer[1] + splitNumer[2]);

        KakaoOption kakaoOption = new KakaoOption();
        kakaoOption.setPfId(PFID);
        kakaoOption.setTemplateId(TEMPLATE_ID);

        HashMap<String, String> variables = new HashMap<>();
        variables.put("#{receive_name}", reminder.getRecipientName());
        variables.put("#{send_name}", reminder.getSenderName());
        variables.put("#{send_date}", reminder.getReceiveDate().toString());

        variables.put("#{letter_url}", reminder.getUrlSlug().toString());
        kakaoOption.setVariables(variables);

        Message message = new Message();
        message.setFrom(SEND_PHONE_NUMBER);
        message.setTo("010" + splitNumer[1] + splitNumer[2]);
        message.setKakaoOptions(kakaoOption);

        try {
            Date date = new Date(reminder.getReceiveDate().toString());

            // Date today = new Date();
            // Date tomorrow = new Date(today.getTime() + 60 * 1000);
            log.info("date {}", date);
            LocalDateTime localDateTime = date.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            // LocalDateTime localDateTime =
            // LocalDateTime.parse(reminder.getReceiveDate().toString(),
            // DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            ZoneOffset zoneOffset = ZoneId.systemDefault().getRules().getOffset(localDateTime);
            Instant instant = localDateTime.toInstant(zoneOffset);
            // send 메소드로 ArrayList<Message> 객체를 넣어도 동작합니다!
            messageService.send(message, instant);
        } catch (NurigoMessageNotReceivedException exception) {
            // 발송에 실패한 메시지 목록을 확인할 수 있습니다!
            System.out.println(exception.getFailedMessageList());
            System.out.println(exception.getMessage());
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }

        return true;
    }

    private void validate(Reminder entity) {
        if (entity == null) {
            log.warn("Entity cannot be null.");
            throw new RuntimeException("Entity cannot be null");
        }
    }

    public Reminder save(Reminder reminder) {
        return reminderRepository.save(reminder);
    }

    public Long getReminderCount() {
        return reminderRepository.count();
    }
}
