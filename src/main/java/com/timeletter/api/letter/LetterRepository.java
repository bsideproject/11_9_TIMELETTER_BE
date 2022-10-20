package com.timeletter.api.letter;

import com.timeletter.api.statistics.LetterStatisticInterface;
import com.timeletter.api.statistics.StatisticInterface;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LetterRepository extends JpaRepository<Letter, String> {
    List<Letter> findAllByUserID(String userId);

    Optional<Letter> findByUrlSlug(String urlSlug);

    Page<Letter> findAllByUserIDAndLetterStatus(String userID, LetterStatus letterStatus, Pageable pageable);

    @Query(value = "select  l.createdAt as date " +
            "from Letter l where l.createdAt between :stDate and :edDate"
    )
    List<LocalDateTime> findGroupByRegDate(@Param("stDate") LocalDateTime stDate, @Param("edDate") LocalDateTime edDate);
}
