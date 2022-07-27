package com.timeletter.api.letter;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LetterRepository extends JpaRepository<Letter,String> {
    List<Letter> findAllByUserID(String userId);
}
