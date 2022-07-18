package com.timeletter.api.letter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class LetterService {

    private final LetterRepository letterRepository;

    public Letter create(final Letter entity){
        validate(entity);

        letterRepository.save(entity);

        log.info("Entity id : {} is saved",entity.getId());

        return letterRepository.findById(entity.getId()).get();
    }

    private void validate(Letter entity) {
        if(entity == null){
            log.warn("Entity cannot be null.");
            throw new RuntimeException("Entity cannot be null");
        }
    }
}
