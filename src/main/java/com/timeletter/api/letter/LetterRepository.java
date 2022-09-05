package com.timeletter.api.letter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LetterRepository extends JpaRepository<Letter,String> {
    List<Letter> findAllByUserID(String userId);
    Page<Letter> findAllByUserIDAndLetterStatus(String userID, LetterStatus letterStatus, Pageable pageable);
}
