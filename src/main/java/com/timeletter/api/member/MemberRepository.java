package com.timeletter.api.member;

import com.timeletter.api.statistics.StatisticInterface;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MemberRepository extends JpaRepository<Member,String> {
    Member findByEmail(String email);
    Boolean existsByEmail(String email);
    Member findByEmailAndPassword(String email,String password);

    @Query(value = "select  m.regDate as date, count(m.regDate) as count " +
            "from Member m where m.regDate between :stDate and :edDate " +
            "group by m.regDate"
    )
    List<StatisticInterface> findGroupByRegDate(@Param("stDate") LocalDate stDate, @Param("edDate") LocalDate edDate);
}
