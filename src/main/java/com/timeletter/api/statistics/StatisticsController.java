package com.timeletter.api.statistics;

import com.timeletter.api.dto.ResponseDTO;
import com.timeletter.api.letter.LetterService;
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
    private final LetterService letterService;

    @GetMapping("/memCount")
    public ResponseEntity<?> memberCountGroupByDate(@RequestParam("stDate") String stDate, @RequestParam("edDate") String edDate) {
        try {
            List<String> statisticInterfaces = memberService.memCountGroupByDate(stDate, edDate);
            return ResponseEntity.ok().body(statisticInterfaces);
        } catch (Exception e) {
            ResponseDTO<Object> responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @GetMapping("/letterCount")
    public ResponseEntity<?> letterCountGroupByDate(@RequestParam("stDate") String stDate, @RequestParam("edDate") String edDate) {
        try {
            List<String> statisticInterfaces = letterService.letterCountGroupByDate(stDate, edDate);
            return ResponseEntity.ok().body(statisticInterfaces);
        } catch (Exception e) {
            ResponseDTO<Object> responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @GetMapping("/letterCount2")
    public ResponseEntity<?> letterCountGroupByDate2(@RequestParam("stDate") String stDate, @RequestParam("edDate") String edDate) {
        try {
            List<String> statisticInterfaces = letterService.letterCountGroupByDate2(stDate, edDate);
            return ResponseEntity.ok().body(statisticInterfaces);
        } catch (Exception e) {
            ResponseDTO<Object> responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }
}
