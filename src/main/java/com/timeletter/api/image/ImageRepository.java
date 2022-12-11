package com.timeletter.api.image;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image,String> {
    Optional<Image> findByLetterId(String letterId);
}
