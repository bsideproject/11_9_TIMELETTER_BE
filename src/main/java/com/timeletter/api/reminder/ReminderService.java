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

import com.timeletter.api.letter.Letter;
import com.timeletter.api.letter.LetterRepository;
import com.timeletter.api.member.Member;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ReminderService {
    @Value("${solapi.api-key}")
    private String apiKey;
    @Value("${solapi.secret-key}")
    private String secretKey;
    @Value("${test.template-id}")
    private String templateId;
    @Value("${test.template-completed-id}")
    private String TemplateCompletedId;
    @Value("${test.pfid}")
    private String pfid;
    @Value("${test.send-phone-number}")
    private String sendPhoneNumber;

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

    DefaultMessageService messageService = NurigoApp.INSTANCE.initialize(apiKey, secretKey, "https:// api.solapi.com");

    public boolean sendReminderComplated(Member member) {

        String[] splitNumer = "82 10-5607-3471".split("-");

        KakaoOption kakaoOption = new KakaoOption();
        kakaoOption.setPfId(pfid);
        kakaoOption.setTemplateId(templateId);

        HashMap<String, String> variables = new HashMap<>();
        variables.put("#{send_name}", member.getUsername());
        variables.put("#{letter_opendate}", "010" + splitNumer[1] + splitNumer[2]);
        kakaoOption.setVariables(variables);

        Message message = new Message();
        message.setFrom(sendPhoneNumber);
        message.setTo(member.getPhoneNumber());
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

    private void validate(Reminder entity) {
        if (entity == null) {
            log.warn("Entity cannot be null.");
            throw new RuntimeException("Entity cannot be null");
        }
    }

    public Reminder save(Reminder reminder) {
        return reminderRepository.save(reminder);
    }
}
