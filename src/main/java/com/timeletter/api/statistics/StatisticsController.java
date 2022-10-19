package com.timeletter.api.statistics;

import com.timeletter.api.dto.ResponseDTO;
import com.timeletter.api.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/statistics")
public class StatisticsController {

    private final MemberService memberService;

    @GetMapping("/memCount")
    public ResponseEntity<?> memberCountGroupByDate(@RequestParam("stDate") String stDate, @RequestParam("edDate") String edDate) {
        try {
            List<StatisticInterface> statisticInterfaces = memberService.memCountGroupByDate(stDate, edDate);
            return ResponseEntity.ok().body(statisticInterfaces);
        } catch (Exception e) {
            ResponseDTO<Object> responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        try {
            return ResponseEntity.ok().body("hello world");
        } catch (Exception e) {
            ResponseDTO<Object> responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }
}
