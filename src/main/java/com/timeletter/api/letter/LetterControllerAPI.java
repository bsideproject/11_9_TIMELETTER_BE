package com.timeletter.api.letter;

import com.timeletter.api.dto.ResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> retrieveLetterList(){
        List<Letter> entities = letterService.findAll();

        List<LetterDTO> dtos = entities.stream().map(LetterDTO::new).collect(Collectors.toList());

        ResponseDTO<LetterDTO> response = ResponseDTO.<LetterDTO>builder().data(dtos).build();

        return ResponseEntity.ok().body(response);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody LetterDTO dto){
        try{
            Letter letter = letterService.create(Letter.toEntity(dto));

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
    public ResponseEntity<?> updateLetter(@RequestBody LetterDTO dto){
        Letter Letter = LetterDTO.toEntity(dto);

        Letter entities = letterService.update(Letter);

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
