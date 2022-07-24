package com.timeletter.api.member;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member,String> {
    Member findByEmail(String email);
    Boolean existsByEmail(String email);
    Member findByEmailAndPassword(String email,String password);
}
