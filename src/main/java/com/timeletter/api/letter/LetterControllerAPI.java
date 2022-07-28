package com.timeletter.api.letter;

import com.timeletter.api.dto.ResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @PostMapping
    public ResponseEntity<?> create(@RequestBody LetterDTO dto,
                                    @AuthenticationPrincipal String userId){
        try{
            Letter letterEntity = Letter.toEntity(dto);
            letterEntity.setUserID(userId);

            Letter letter = letterService.create(letterEntity);

            List<LetterDTO> dtos = new ArrayList<>();
            dtos.add(new LetterDTO(letter));

            ResponseDTO<LetterDTO> response = ResponseDTO.<LetterDTO>builder().data(dtos).build();

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
