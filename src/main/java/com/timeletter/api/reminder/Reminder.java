package com.timeletter.api.reminder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
public class Reminder {
    // Member와 Letter에서 값을 안가져오고 굳이 한번에 프론트엔드에서 받아서 처리하는 이유는,
    // 컬럼 한두개 추가하는것보다 쌓여가는 데이터베이스에서 쿼리를 Member, Letter 각각 한번씩
    // 날리는것이 오히려 비용과 처리속도 면에서 비효율적이라고 생각하였음.
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "id")
    private String reminderId;

    @Column(nullable = false)
    private String letterId;

    @Column(nullable = false)
    private String userId;

    private String senderName;

    private LocalDateTime sendDate;

    private String receivedPhoneNumber;

    @ColumnDefault("false")
    private Boolean isSended;

}
