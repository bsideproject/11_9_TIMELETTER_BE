package com.timeletter.admin.statistics;

import com.timeletter.api.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("statistics")
public class StatisticsController {

    private MemberService memberService;
}
