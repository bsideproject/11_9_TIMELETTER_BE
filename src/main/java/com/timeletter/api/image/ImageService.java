package com.timeletter.api.image;

import com.timeletter.api.dto.ResponseDTO;
import com.timeletter.api.letter.Letter;
import com.timeletter.api.letter.LetterDTO;
import com.timeletter.api.letter.LetterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final LetterService letterService;


    /**
     * 편지 아이디에 대한 이미지 업로드
     *
     * @param letterId 업로드하고자하는 편지 아이디
     * @param file 이미지 파일
     * @return
     */
    public ResponseEntity<?> processImageUpload(String letterId, MultipartFile file) {
        try {
            String savedImageId = this.saveImage(file, letterId);

            Letter byLetterId = letterService.findByLetterId(letterId);

            List<LetterDTO> data = new ArrayList<>();
            LetterDTO letterDTO = new LetterDTO(byLetterId);
            letterDTO.setImageId(savedImageId);
            data.add(letterDTO);

            return returnOkRequest(data);
        }catch (Exception e){
            return returnBadRequest(e);
        }
    }



    /**
     * 이미지 상세조회
     *
     * @param imageId 띄우고자 하는 이미지 아이디
     * @return 이미지
     */
    public ResponseEntity<?> processFindImageById(String imageId) {
        try{
            Image byId = this.findById(imageId);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", byId.getMimetype());
            headers.add("Content-Length", String.valueOf(byId.getData().length));

            return ResponseEntity.ok().headers(headers).body(byId.getData());
        }catch (Exception e){
            return returnBadRequest(e);
        }
    }



    public String saveImage(MultipartFile file, String letterId) throws IOException {
        Letter letter = letterService.findByLetterId(letterId);

        Image image = Image.builder()
                .mimetype(file.getContentType())
                .original_name(file.getOriginalFilename())
                .data(file.getBytes())
                .letter(letter)
                .build();

        return save(image).getId();
    }

    private ResponseEntity<?> returnBadRequest(Exception e) {
        ResponseDTO<LetterDTO> response = ResponseDTO.<LetterDTO>builder().error(e.toString()).build();
        return ResponseEntity.badRequest().body(response);
    }

    private ResponseEntity<?> returnOkRequest(List<LetterDTO> data) {
        ResponseDTO<LetterDTO> response = ResponseDTO.<LetterDTO>builder().data(data).build();
        return ResponseEntity.ok().body(response);
    }



    @Transactional
    public Image save(Image image) {
        return imageRepository.save(image);
    }

    @Transactional
    public Image findById(String id){
        return imageRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public Optional<Image> findByLetterId(String letterId) {
        return imageRepository.findByLetterId(letterId);
    }
}
