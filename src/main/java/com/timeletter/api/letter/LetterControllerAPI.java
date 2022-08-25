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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
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

@Slf4j
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
            log.info("편지 생성 작업 로직 시작합니다.");
            log.info("Request Body : " + dto);
            Letter letterEntity = Letter.toEntity(dto);
            letterEntity.setUserID(userId);
            String letterId = "";

            // 임시저장상태의 요청이 왔을 경우
            if(letterEntity.getLetterStatus().equals(LetterStatus.DRAFT)){
                letterId = letterService.create(letterEntity);
                log.info("편지 생성 완료");
                return ResponseEntity.ok().body("편지 생성 완료");
            }
            // 저장완료, 전송완료 상태의 요청이 왔을 경우
            if(letterEntity.getLetterStatus().equals(LetterStatus.DONE) || letterEntity.getLetterStatus().equals(LetterStatus.SUBMIT)){
                letterId = letterService.update(letterEntity);
                log.info("편지 상태 업데이트 완료");
            }

            Letter letter = letterService.findByLetterId(letterId);

            List<LetterDTO> result = new ArrayList<>();
            result.add(new LetterDTO(letter));

            ResponseDTO<LetterDTO> response = ResponseDTO.<LetterDTO>builder().data(result).build();
            return ResponseEntity.ok().body(response);

        }catch (Exception e){
            ResponseDTO<LetterDTO> response = ResponseDTO.<LetterDTO>builder().error(e.toString()).build();
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

        String letterId = letterService.update(letter);
        Letter entities = letterService.findByLetterId(letterId);

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

    @Operation(summary = "이미지 업로드", description = "편지에 이미지를 업로드합니.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK !!"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST !!"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN !!"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND !!"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR !!")
    })
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

    @Operation(summary = "이미지 상세 조회", description = "이미지를 상세 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK !!"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST !!"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN !!"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND !!"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR !!")
    })
    @GetMapping("/imageView/{id}")
    public ResponseEntity<?> findImageById(@PathVariable("id") String imageId){

        try{
            Image byId = imageService.findById(imageId);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", byId.getMimetype());
            headers.add("Content-Length", String.valueOf(byId.getData().length));

            return ResponseEntity.ok().headers(headers).body(byId.getData());

        }catch (Exception e){
            String error = e.getMessage();
            ResponseDTO<LetterDTO> response = ResponseDTO.<LetterDTO>builder().error(error).build();

            return ResponseEntity.badRequest().body(response);
        }
    }

}
