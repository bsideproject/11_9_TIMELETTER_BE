package com.timeletter.api.letter;

import com.timeletter.api.dto.ResponseDTO;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = {"Letter Info"},description = "편지 관련 서비스")
@RestController
@AllArgsConstructor
@RequestMapping("/v1/letter")
public class LetterControllerAPI {

    private final LetterService letterService;

    @GetMapping
    public ResponseEntity<?> retrieveLetterList(@AuthenticationPrincipal String userId){
        List<Letter> entities = letterService.findAllByUserId(userId);

        List<LetterDTO> dtos = entities.stream().map(LetterDTO::new).collect(Collectors.toList());

        ResponseDTO<LetterDTO> response = ResponseDTO.<LetterDTO>builder().data(dtos).build();

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findLetterByLetterId(@PathVariable("id") String letterId){
        try{
            Letter byLetterId = letterService.findByLetterId(letterId);

            List<LetterDTO> dtos = new ArrayList<>();
            dtos.add(new LetterDTO(byLetterId));

            ResponseDTO<LetterDTO> response = ResponseDTO.<LetterDTO>builder().data(dtos).build();

            return ResponseEntity.ok().body(response);
        }catch (Exception e){
            String error = e.getMessage();
            ResponseDTO<LetterDTO> response = ResponseDTO.<LetterDTO>builder().error(error).build();

            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/receive/{id}")
    public ResponseEntity<?> receiveLetter(@PathVariable("id") String letterId){
        try {
            Letter byLetterId = letterService.findByLetterId(letterId);
            if(byLetterId.getReceivedDate().isAfter(LocalDateTime.now())){
                List<LetterDTO> dtos = new ArrayList<>();
                dtos.add(new LetterDTO(byLetterId));

                ResponseDTO<LetterDTO> response = ResponseDTO.<LetterDTO>builder().data(dtos).build();

                return ResponseEntity.ok().body(response);
            }else{
                String fail_msg = "아직 시간이 도래하지 않았습니다.";
                ResponseDTO<LetterDTO> response = ResponseDTO.<LetterDTO>builder().error(fail_msg).build();

                return ResponseEntity.badRequest().body(response);
            }
        }catch (Exception e){
            String error = e.getMessage();
            ResponseDTO<LetterDTO> response = ResponseDTO.<LetterDTO>builder().error(error).build();

            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody LetterDTO dto,
                                    @AuthenticationPrincipal String userId){
        try{
            Letter letterEntity = Letter.toEntity(dto);
            letterEntity.setUserID(userId);
            Letter letter = new Letter();

            // 임시저장상태의 요청이 왔을 경우
            if(dto.getLetterStatus().equals(LetterStatus.DRAFT)){
                letter = letterService.create(letterEntity);
            }
            // 저장완료, 전송완료 상태의 요청이 왔을 경우
            if(dto.getLetterStatus().equals(LetterStatus.DONE) || dto.getLetterStatus().equals(LetterStatus.SUBMIT)){
                letter = letterService.update(letterEntity);
            }

            List<LetterDTO> result = new ArrayList<>();
            result.add(new LetterDTO(letter));

            ResponseDTO<LetterDTO> response = ResponseDTO.<LetterDTO>builder().data(result).build();
            return ResponseEntity.ok().body(response);

        }catch (Exception e){
            ResponseDTO<LetterDTO> response = ResponseDTO.<LetterDTO>builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping
    public ResponseEntity<?> updateLetter(@RequestBody LetterDTO dto,
                                          @AuthenticationPrincipal String userId){
        Letter letter = LetterDTO.toEntity(dto);
        letter.setUserID(userId);

        Letter entities = letterService.update(letter);

        List<LetterDTO> dtos = new ArrayList<>();
        dtos.add(new LetterDTO(entities));

        ResponseDTO<LetterDTO> response = ResponseDTO.<LetterDTO>builder().data(dtos).build();

        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping
    public ResponseEntity<?> delete(@RequestBody LetterDTO dto){
        try {
            Letter entity = LetterDTO.toEntity(dto);

            letterService.delete(entity);

            List<LetterDTO> dtos = new ArrayList<>();
            dtos.add(new LetterDTO(entity));

            ResponseDTO<LetterDTO> response = ResponseDTO.<LetterDTO>builder().data(dtos).build();

            return ResponseEntity.ok().body(response);
        }catch (Exception e){
            String error = e.getMessage();
            ResponseDTO<LetterDTO> response = ResponseDTO.<LetterDTO>builder().error(error).build();

            return ResponseEntity.badRequest().body(response);
        }
    }
}
