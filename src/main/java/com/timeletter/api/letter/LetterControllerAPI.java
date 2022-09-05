package com.timeletter.api.letter;

import com.timeletter.api.dto.PageRequestDTO;
import com.timeletter.api.image.ImageService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;



@CrossOrigin("*")
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
        return letterService.processRetrieveLetterList(userId);
    }


    @Operation(summary = "편지 리스트 조회", description = "회원이 보유한 편지 리스트 전체를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK !!"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST !!"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN !!"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND !!"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR !!")
    })
    @GetMapping("/version2")
    public ResponseEntity<?> retrieveLetterList2(PageRequestDTO dto, @AuthenticationPrincipal String userId){
        return letterService.processRetrieveLetterList2(dto, userId);
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
        return letterService.processFindLetterById(letterId);
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
        return letterService.processReceiveLetter(letterId);
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
        return letterService.processCreate(dto,userId);
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
        return letterService.processUpdateLetter(dto,userId);
    }



    @Operation(summary = "수신인 수정", description = "회원의 편지 수신인을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK !!"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST !!"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN !!"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND !!"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR !!")
    })
    @PutMapping("/updateReceiver")
    public ResponseEntity<?> updateLetterReceiver(@RequestBody LetterDTO dto){
        return letterService.processUpdateLetterReceiver(dto.getId(),dto.getReceivedPhoneNumber());
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
        return letterService.processDelete(dto);
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
    public ResponseEntity<?> imageUpload(@RequestParam("letterId") String letterId,
                                    @RequestParam("file") MultipartFile file) {
        return imageService.processImageUpload(letterId,file);
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
        return imageService.processFindImageById(imageId);
    }

}
