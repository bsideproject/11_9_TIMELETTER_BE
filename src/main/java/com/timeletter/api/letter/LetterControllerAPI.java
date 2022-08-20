package com.timeletter.api.letter;

import com.timeletter.api.dto.ResponseDTO;
import com.timeletter.api.image.Image;
import com.timeletter.api.image.ImageService;
import com.timeletter.api.member.Member;
import com.timeletter.api.member.MemberService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = {"Letter Info"}, description = "편지 관련 API")
@RestController
@AllArgsConstructor
@RequestMapping("/v1/letter")
public class LetterControllerAPI {

    private final LetterService letterService;
    private final ImageService imageService;

    @Operation(summary = "편지 리스트 조회", description = "회원이 보유한 편지 리스트 전체를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK !!"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST !!"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN !!"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND !!"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR !!")
    })
    @GetMapping
    public ResponseEntity<?> retrieveLetterList(@AuthenticationPrincipal String userId){
        List<Letter> entities = letterService.findAllByUserId(userId);

        List<LetterDTO> dtos = entities.stream().map(LetterDTO::new).collect(Collectors.toList());

        ResponseDTO<LetterDTO> response = ResponseDTO.<LetterDTO>builder().data(dtos).build();

        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "편지 상세 조회", description = "회원이 보유한 편지 상세 내용을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK !!"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST !!"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN !!"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND !!"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR !!")
    })
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

    @Operation(summary = "편지 받은 사람이 조회", description = "회원이 전달받은 편지 상세 내용을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK !!"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST !!"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN !!"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND !!"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR !!")
    })
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

    @Operation(summary = "편지 생성", description = "편지 생성.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK !!"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST !!"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN !!"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND !!"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR !!")
    })
    @PostMapping
    public ResponseEntity<?> create(@RequestBody LetterDTO dto,
                                    @AuthenticationPrincipal String userId){
        try{
            Letter letterEntity = Letter.toEntity(dto);
            letterEntity.setUserID(userId);
            Letter letter = new Letter();

            // 임시저장상태의 요청이 왔을 경우
            if(letterEntity.getLetterStatus().equals(LetterStatus.DRAFT)){
                letter = letterService.create(letterEntity);
            }
            // 저장완료, 전송완료 상태의 요청이 왔을 경우
            if(letterEntity.getLetterStatus().equals(LetterStatus.DONE) || letterEntity.getLetterStatus().equals(LetterStatus.SUBMIT)){
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

    @Operation(summary = "편지 수정", description = "회원의 편지 상세 내용을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK !!"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST !!"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN !!"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND !!"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR !!")
    })
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

    @Operation(summary = "편지 삭제", description = "편지를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK !!"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST !!"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN !!"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND !!"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR !!")
    })
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

    @PostMapping(value = "/imageUpload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> handleFileUpload(@RequestParam("letterId") String letterId,
                                    @RequestParam("file") MultipartFile file) throws IOException {
        try {
            String savedImageId = imageService.save(file, letterId);

            Letter byLetterId = letterService.findByLetterId(letterId);

            List<LetterDTO> dtos = new ArrayList<>();
            LetterDTO letterDTO = new LetterDTO(byLetterId);
            letterDTO.setImageId(savedImageId);
            dtos.add(letterDTO);

            ResponseDTO<LetterDTO> response = ResponseDTO.<LetterDTO>builder().data(dtos).build();

            return ResponseEntity.ok().body(response);

        }catch (Exception e){
            String error = e.getMessage();
            ResponseDTO<LetterDTO> response = ResponseDTO.<LetterDTO>builder().error(error).build();

            return ResponseEntity.badRequest().body(response);
        }
    }
}
