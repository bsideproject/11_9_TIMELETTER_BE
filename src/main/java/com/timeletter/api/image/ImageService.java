package com.timeletter.api.image;

import com.timeletter.api.letter.Letter;
import com.timeletter.api.letter.LetterDTO;
import com.timeletter.api.letter.LetterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.io.IOException;

@Service
@Transactional
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final LetterService letterService;

    public String save(MultipartFile file, String letterId) throws IOException {

        Letter letter = letterService.findByLetterId(letterId);

        Image image = Image.builder()
                .mimetype(file.getContentType())
                .original_name(file.getOriginalFilename())
                .data(file.getBytes())
                .letter(letter)
                .build();

        return imageRepository.save(image).getId();
    }

    public Image findById(String id){
        return imageRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }
}
