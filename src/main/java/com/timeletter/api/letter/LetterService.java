package com.timeletter.api.letter;

import com.timeletter.api.dto.PageRequestDTO;
import com.timeletter.api.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LetterService {

    private final LetterRepository letterRepository;

    /**
     * 임시로 편지 상태 변경하고자 만듬
     *
     * @param urlSlug 수신인 편지를 수정할 편지 ID
     * @return 수정된 편지 Entity
     */
    public ResponseEntity<?> processUpdateLetterStatus(String urlSlug) {
        try {
            Optional<Letter> byLetterUrlSlug = findByUrlSlug(urlSlug);
            List<LetterDTO> data = new ArrayList<>();
            byLetterUrlSlug.ifPresent(letter -> {
                letter.setLetterStatus(LetterStatus.DONE);
                save(letter);
                data.add(new LetterDTO(letter));
            });

            return returnOkRequest(data);
        } catch (Exception e) {
            return returnBadRequest(e);
        }
    }

    /**
     * 편지 내용 상세 조회
     *
     * @param letterId 상세조회하고자 하는 편지 아이디
     * @return ResponseEntity 응답 Entity
     */
    public ResponseEntity<?> processFindLetterById(String letterId) {
        try {
            Optional<Letter> byLetterId = retrieve(letterId);

            List<LetterDTO> data = new ArrayList<>();
            byLetterId.ifPresent(letter -> {
                data.add(new LetterDTO(letter));
            });

            return returnOkRequest(data);
        } catch (Exception e) {
            return returnBadRequest(e);
        }
    }

    /**
     * 편지 내용 Urlslug로 상세 조회
     *
     * @param urlSlug 상세조회하고자 하는 편지 urlSlug
     * @return ResponseEntity 응답 Entity
     */
    public ResponseEntity<?> processFindLetterByUrlSlug(String urlSlug) {
        try {
            Optional<Letter> byLetterUrlSlug = findByUrlSlug(urlSlug);

            List<LetterDTO> data = new ArrayList<>();
            byLetterUrlSlug.ifPresent(letter -> {
                data.add(new LetterDTO(letter));
            });

            return returnOkRequest(data);
        } catch (Exception e) {
            return returnBadRequest(e);
        }
    }

    /**
     * 해당 유저에 해당하는 편지 리스트 조회
     *
     * @param userId 편지 리스트 조회하고자하는 유저 아이디
     * @return 편지 리스트
     */
    public ResponseEntity<?> processRetrieveLetterList(String userId) {
        try {
            List<Letter> entities = this.findAllByUserId(userId);

            List<LetterDTO> data = entities.stream().map(LetterDTO::new).collect(Collectors.toList());

            return returnOkRequest(data);
        } catch (Exception e) {
            return returnBadRequest(e);
        }
    }

    /**
     * 해당 유저에 해당하는 편지 리스트 조회
     *
     *
     * @param letterStatus
     * @param userId       편지 리스트 조회하고자하는 유저 아이디
     * @return 편지 리스트
     */
    public ResponseEntity<?> processRetrieveLetterList2(PageRequest pageRequest, String letterStatus, String userId) {
        try {
            // Pageable pageable = requestDTO.getPageable(Sort.by("id").descending());
            Page<Letter> data = letterRepository.findAllByUserIDAndLetterStatus(userId,
                    LetterStatus.valueOf(letterStatus), pageRequest);

            // ResponseDTO<Letter> response =
            // ResponseDTO.<Letter>builder().pageData(data).build();
            // return ResponseEntity.ok().body(response);
            return ResponseEntity.ok().body(data);
        } catch (Exception e) {
            return returnBadRequest(e);
        }
    }

    /**
     * 받는 사람 입장에서의 편지 상세 내용
     *
     * @param letterId 편지 아이디
     * @return 편지 상세 내용
     */
    public ResponseEntity<?> processReceiveLetter(String letterId) {
        try {
            Optional<Letter> byLetterId = retrieve(letterId);
            List<LetterDTO> data = new ArrayList<>();
            byLetterId.ifPresent(letter -> {
                if (isOpenTime(letter)) {
                    data.add(new LetterDTO(letter));
                }
                if (isNotOpenTime(letter)) {
                    LetterDTO letterDTO = new LetterDTO(letter);
                    letterDTO.setLetterStatus(LetterStatus.NOT_YET);
                    data.add(letterDTO);
                }
            });

            return returnOkRequest(data);
        } catch (Exception e) {
            return returnBadRequest(e);
        }
    }

    /**
     * 편지 생성 프로세스
     *
     * @param dto    편지 DTO
     * @param userId 사용자 이메일
     * @return 생성 이후 편지 Entity
     */
    public ResponseEntity<?> processCreate(LetterDTO dto, String userId) {
        try {
            Letter letterEntity = Letter.toEntity(dto);
            letterEntity.setUserID(userId);

            int leftLimit = 48; // numeral '0'
            int rightLimit = 122; // letter 'z'
            int targetStringLength = 32;
            Random random = new Random();
            String generatedString = random.ints(leftLimit, rightLimit + 1)
                    .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                    .limit(targetStringLength)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();

            letterEntity.setUrlSlug(generatedString);
            String letterId = "";

            // 임시저장상태의 요청이 왔을 경우
            if (isDraft(letterEntity)) {
                letterId = this.create(letterEntity);
                log.info("편지 생성 완료");
            }
            // 저장완료, 전송완료 상태의 요청이 왔을 경우
            if (isDone(letterEntity) || isSubmit(letterEntity)) {
                letterId = this.update(letterEntity);
                log.info("편지 상태 업데이트 완료 : " + letterEntity.getLetterStatus());
            }

            List<LetterDTO> data = new ArrayList<>();
            retrieve(letterId).ifPresent(letter -> {
                data.add(new LetterDTO(letter));
            });

            return returnOkRequest(data);
        } catch (Exception e) {
            return returnBadRequest(e);
        }
    }

    /**
     * 편지 수정 프로세스
     *
     * @param dto    편지 DTO
     * @param userId 사용자 이메일
     * @return 생성 이후 편지 Entity
     */
    public ResponseEntity<?> processUpdateLetter(LetterDTO dto, String userId) {
        try {
            Letter entity = LetterDTO.toEntity(dto);
            entity.setUserID(userId);

            String letterId = this.update(entity);

            List<LetterDTO> data = new ArrayList<>();
            retrieve(letterId).ifPresent(letter -> {
                data.add(new LetterDTO(letter));
            });

            return returnOkRequest(data);
        } catch (Exception e) {
            return returnBadRequest(e);
        }
    }

    /**
     * 편지 수신인 전화번호를 변경하고자 한다.
     *
     * @param letterId    수신인 편지를 수정할 편지 ID
     * @param phoneNumber 변경하고자하는 전화번호
     * @return 수정된 편지 Entity
     */
    public ResponseEntity<?> processUpdateLetterReceiver(String letterId, String phoneNumber) {
        try {
            Letter entity = findByLetterId(letterId);

            this.updateReceiver(entity, phoneNumber);

            List<LetterDTO> data = new ArrayList<>();
            retrieve(letterId).ifPresent(letter -> {
                data.add(new LetterDTO(letter));
            });

            return returnOkRequest(data);
        } catch (Exception e) {
            return returnBadRequest(e);
        }
    }

    /**
     * 편지 삭제 프로세스
     *
     * @param dto 편지 DTO
     * @return 생성 이후 편지 Entity
     */
    public ResponseEntity<?> processDelete(LetterDTO dto) {
        Letter entity = LetterDTO.toEntity(dto);
        try {
            validate(entity);
            delete(entity);

            List<LetterDTO> data = new ArrayList<>();
            data.add(new LetterDTO(entity));

            return returnOkRequest(data);
        } catch (Exception e) {
            log.error("Error deleting entity ", entity.getId(), e);
            return returnBadRequest(e);
        }
    }

    /**
     * 편지를 열 수 있는 시간인지 확인
     *
     * @param letter 편지 Entity
     * @return 편지 시간이 열 수 있으면 false, 없으면 true
     */
    private boolean isNotOpenTime(Letter letter) {
        return letter.getReceivedDate().isBefore(LocalDateTime.now());
    }

    /**
     * 편지를 열 수 있는 시간인지 확인
     *
     * @param letter 편지 Entity
     * @return 편지 시간이 열 수 있으면 true, 없으면 false
     */
    private boolean isOpenTime(Letter letter) {
        return letter.getReceivedDate().isAfter(LocalDateTime.now());
    }

    /**
     * 편지를 생성하고, 아이디를 반환한다.
     *
     * @param entity 편지 엔티티
     * @return saveId 생성된 편지 아이디
     */
    public String create(final Letter entity) {
        validate(entity);
        Letter save = save(entity);

        String saveId = save.getId();
        log.info("Entity saveId : {} is saved", saveId);

        return saveId;
    }

    /**
     * 편지내용을 업데이트 한다.
     *
     * @param entity 편지 엔티티
     * @return saveId 수정된 편지 아이디
     */
    public String update(final Letter entity) {
        validate(entity);

        final Optional<Letter> original = retrieve(entity.getId());

        original.ifPresent(letter -> {
            // letter.setTitle(entity.getTitle());
            letter.setContent(entity.getContent());
            letter.setLetterStatus(entity.getLetterStatus());
            letter.setReceivedDate(entity.getReceivedDate());
            // letter.setReceivedPhoneNumber(entity.getReceivedPhoneNumber());
            letter.setSenderName(entity.getSenderName());
            letter.setReceiverName(entity.getReceiverName());
            save(letter);
        });

        return entity.getId();
    }

    /**
     * 편지 수신인을 업데이트 한다.
     *
     * @param entity 편지 엔티티
     * @return saveId 수정된 편지 아이디
     */
    public String updateReceiver(final Letter entity, String newPhoneNumber) {
        validate(entity);

        final Optional<Letter> original = retrieve(entity.getId());

        original.ifPresent(letter -> {
            // letter.setReceivedPhoneNumber(newPhoneNumber);
            save(letter);
        });

        return entity.getId();
    }

    private ResponseEntity<?> returnBadRequest(Exception e) {
        ResponseDTO<LetterDTO> response = ResponseDTO.<LetterDTO>builder().error(e.toString()).build();
        return ResponseEntity.badRequest().body(response);
    }

    private ResponseEntity<?> returnOkRequest(List<LetterDTO> data) {
        // ResponseDTO<LetterDTO> response =
        // ResponseDTO.<LetterDTO>builder().data(data).build();
        // return ResponseEntity.ok().body(response);
        return ResponseEntity.ok().body(data);
    }

    private boolean isSubmit(Letter letterEntity) {
        return letterEntity.getLetterStatus().equals(LetterStatus.SUBMIT);
    }

    private boolean isDone(Letter letterEntity) {
        return letterEntity.getLetterStatus().equals(LetterStatus.DONE);
    }

    private boolean isDraft(Letter letterEntity) {
        return letterEntity.getLetterStatus().equals(LetterStatus.DRAFT);
    }

    private void validate(Letter entity) {
        if (entity == null) {
            log.warn("Entity cannot be null.");
            throw new RuntimeException("Entity cannot be null");
        }
    }

    @Deprecated
    @Transactional
    public List<Letter> findAll() {
        return letterRepository.findAll();
    }

    @Transactional
    public Optional<Letter> retrieve(final String id) {
        return letterRepository.findById(id);
    }

    @Transactional
    public Optional<Letter> findByUrlSlug(String urlSlug) {
        return letterRepository.findByUrlSlug(urlSlug);
    }

    @Transactional
    public List<Letter> findAllByUserId(String userId) {
        return letterRepository.findAllByUserID(userId);
    }

    @Transactional
    public Letter findByLetterId(String letterId) {
        return letterRepository.findById(letterId).orElseThrow(
                () -> new IllegalArgumentException(String.format("아이디 : {} 에 해당하는 편지 Entity가 존재하지 않습니다.", letterId)));
    }

    @Transactional
    public Letter save(Letter letter) {
        return letterRepository.save(letter);
    }

    @Transactional
    public void delete(Letter letter) {
        letterRepository.delete(letter);
    }

}
