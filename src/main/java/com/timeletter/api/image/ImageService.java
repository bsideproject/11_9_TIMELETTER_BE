package com.timeletter.api.image;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;

    public void save(Image image){
        imageRepository.save(image);
    }

    public Image findById(String id){
        return imageRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }
}
