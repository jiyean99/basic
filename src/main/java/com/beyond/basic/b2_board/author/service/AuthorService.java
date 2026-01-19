package com.beyond.basic.b2_board.author.service;

import com.beyond.basic.b2_board.author.domain.Author;
import com.beyond.basic.b2_board.author.dto.AuthorCreateDto;
import com.beyond.basic.b2_board.author.dto.AuthorDetailDto;
import com.beyond.basic.b2_board.author.dto.AuthorListDto;
import com.beyond.basic.b2_board.author.repository.AuthorJdbcRepository;
import com.beyond.basic.b2_board.author.repository.AuthorMemoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

/*
 * =========================================================
 * AuthorService
 * =========================================================
 *
 * TODO [역할]
 * - Author 관련 비즈니스 로직을 담당한다.
 * - Controller로부터 DTO를 전달받아 Domain(Entity)을 조립하고,
 *   Repository를 통해 저장/조회한 결과를 다시 DTO로 변환해 반환한다.
 *
 * TODO [@Service]
 * - @Service는 컴포넌트 스캔 대상이며, 기본 스코프는 singleton이라 스프링 컨텍스트에서 1개의 인스턴스로 생성/관리된다.
 *
 * TODO [DI(의존성 주입) 방법]
 * (1) 필드주입 (@Autowired)
 *  - final 키워드 사용불가로 안정성을 꾀하기 힘듦
 * (2) 생성자 주입 방식 (가장 많이 사용하는 방식이다)
 *  - 안정성 향상을 위해 final 키워드를 사용하여 상수로 사용 가능 (*final은 반드시 초기화를 해줘야하기때문에 Autowired를 쓰면서 해당 키워드를 사용할 수 없음)
 *  - 다형성 구현 가능(interface 사용 가능, 스펙만을 정의한 구조)
 *  - 순환 참조 방지(컴파일 타임에 에러 check)
 * (3) Lombok @RequiredArgsConstructor 기반 생성자 주입
 *  - 반드시 초기화 되어야하는 필드(final 상수 등)를 대상으로 생성자를 자동생성해주는 어노테이션
 *  - 위 어노테이션 선언 시 생성자 주입방식으로 의존성이 주입된다.
 *  - 다형성 설계는 불가하다
 *
 * TODO [@RequiredArgsConstructor]
 * - 초기화되지 않은 final 필드(또는 @NonNull 필드)를 대상으로 생성자를 자동 생성한다.
 * - 이 생성자가 스프링에 의해 사용되면서 “생성자 주입 방식”으로 DI가 이뤄진다.
 *
 * TODO [생성자 관련 어노테이션]
 * - NoArgus : 매개변수 없는 생성자
 * - AllArgs : 모든 필드를 초기화하는 생성자
 * - RequiredArgsConstructor : 반드시 초기화 되어야하는 필드를 초기화하는 생성자
 *
 * TODO [순환 참조 주의]
 * - S1이 S2를 필요로 하고, S2도 S1을 필요로 하면(서로 주입) 빈 생성 순서를 결정할 수 없어 순환 참조 문제가 발생한다.
 * - S1 → S2 → S3 → S1 처럼 길게 이어지는 형태도 가능하므로, 설계 단계에서 의존 관계를 끊어줘야 한다(역할 분리/별도 서비스로 분리 등).
 * */

@Service
// DI(의존성) 주입 방법 - 3. RequiredArgsConstructor 어노테이션 사용
//@RequiredArgsConstructor
public class AuthorService {

    /// DI(의존성) 주입 방법 - 1. Autowired
//    @Autowired
//    private AuthorMemoryRepository authorMemoryRepository;

    /*
     * =========================================================
     * 의존성: AuthorMemoryRepository
     * =========================================================
     *
     * [왜 final인가]
     * - AuthorService는 AuthorMemoryRepository 없이는 동작할 수 없는 “필수 의존성”이다.
     * - final로 선언하면 생성 시점에 반드시 주입되어야 하므로(불완전한 객체 생성 방지),
     *   의존성이 누락된 상태로 서비스가 사용되는 상황을 막을 수 있다.
     *
     * [과거 실습에서 겪었던 문제(리셋 이슈)]
     * - 서비스 메서드 호출 때마다 레포지토리를 new로 생성하는 구조였다면,
     *   레포지토리 내부의 in-memory 데이터(List) 등이 매번 초기 상태로 돌아가
     *   “저장했는데 다음 호출에서 조회가 안 되는” 문제가 생길 수 있었다.
     *
     * [현재 구조(임시 해결 → DI 적용)]
     * - 현재는 스프링 DI로 AuthorMemoryRepository를 주입받아 재사용하는 구조로 변경했다.
     * - 즉 매번 new로 만들지 않고, 스프링이 관리하는 인스턴스를 주입받아 사용한다.
     */
    /// DI(의존성) 주입 방법 - 2. 생성자 주입 방식
//    private final AuthorMemoryRepository authorMemoryRepository;
//
//    @Autowired // 해당 생성자를 주입받겠다는 의미로 해당 어노테이션 추가(생성자가 하나밖에 없을 때에는 생략 가능)
//    public AuthorService() {
//        this.authorMemoryRepository = new AuthorMemoryRepository();
//    }

    private final AuthorJdbcRepository authorJdbcRepository;

    @Autowired // 해당 생성자를 주입받겠다는 의미로 해당 어노테이션 추가(생성자가 하나밖에 없을 때에는 생략 가능)
    public AuthorService(AuthorJdbcRepository authorJdbcRepository) {
        this.authorJdbcRepository = authorJdbcRepository;
    }

    /// DI(의존성) 주입 방법 - 3. RequiredArgsConstructor 어노테이션 사용
    // private final AuthorMemoryRepository authorMemoryRepository;

    /*
     * =========================================================
     * 객체 조립(2가지)
     * =========================================================
     * TODO [객체 직접 조립]
     * (1) 생성자만을 활용한 객체 조립
     * - id를 사용하지 않고싶다면 생성자 오바라이딩을 해야한다. 매번 생성자 오버라이딩을 하면 너무 비효율적임
     * (2) Builder 패턴을 활용하여 객체 조립
     * - 매개변수 개수의 유연성
     * - 매개변수 순서의 유연성
     * - 빌더패턴은 AllArgs 생성자 기반으로 동작
     *
     * TODO [toEntity, fromEntity 패턴을 통한 객체 조립]
     * - 객체 조립이라는 반복적인 작업을 별도의 코드로 떼어내 공통화하는 작업
     * - DTO에서 공통화 작업을 수행함
     * - fromEntity는 아직 DTO객체가 만들어지지 않은 상태이므로, static 메서드로 설계
     */
    public void save(AuthorCreateDto dto) {
        /// 1-1. 생성자만을 활용한 객체 조립
        // Author author = new Author(null, dto.getName(), dto.getEmail(), dto.getPassword());

        /// 1-2. Builder 패턴 사용하여 조립 - Author 도메인 내 어노테이션 추가
//        Author author = Author.builder()
//                .email(dto.getEmail())
//                .name(dto.getName())
//                .password(dto.getPassword())
//                .build();

        /// 2. toEntity로 조립
        Author author = dto.toEntity();

        /// 1. AuthorMemoryRepository 사용
//        authorMemoryRepository.save(author);

        /// 2. AuthorJdbcRepository 사용
        authorJdbcRepository.save(author);
    }

    public List<AuthorListDto> findAll() {
        /// [기존 방식]
//        List<Author> authorList = authorMemoryRepository.findAll();
//        List<AuthorListDto> authorListDtoList = new ArrayList<>();
//        for (Author a : authorList) {
//            AuthorListDto dto = new AuthorListDto(a.getId(), a.getName(), a.getEmail());
//            authorListDtoList.add(dto);
//        }
//        return authorListDtoList;

        /// [fromEntity 방식으로 객체 조립 방식]
//        List<Author> authorList = authorMemoryRepository.findAll();
//        List<AuthorListDto> authorListDtoList = new ArrayList<>();
//        for (Author author: authorList){
//            AuthorListDto dto = AuthorListDto.fromEntity(author);
//            authorListDtoList.add(dto);
//        }
//        return authorListDtoList;

        /// [fromEntity 방식+stream api]
        /// 1. AuthorMemoryRepository 사용
//        return authorMemoryRepository
//                .findAll()
//                .stream()
//                .map(a -> AuthorListDto.fromEntity(a))
//                .collect(Collectors.toList());

        /// 2. AuthorJdbcRepository 사용
        return authorJdbcRepository
                .findAll()
                .stream()
                .map(a -> AuthorListDto.fromEntity(a))
                .collect(Collectors.toList());
    }

    public AuthorDetailDto findById(Long id) {
        /// 1. AuthorMemoryRepository 사
//        Optional<Author> optionalAuthor = authorMemoryRepository.findById(id);
        /// 2. AuthorJdbcRepository 사용
        Optional<Author> optionalAuthor = authorJdbcRepository.findById(id);
        Author author = optionalAuthor.orElseThrow(() -> new NoSuchElementException("Entity is not found")); // 추후 JPA 의존성 설치 후에는 Entity가 없다는 에러를 던질것이다.

        /// 1-1. DTO 직접 조립(생성자만을 활용한 객체 조립)
//        AuthorDetailDto dto = new AuthorDetailDto(author.getId(), author.getName(), author.getEmail(), author.getPassword());
//        return dto;

        /// 1-2. 빌더패턴으로 조립(개선)
//        AuthorDetailDto dto = AuthorDetailDto.builder()
//                .id(author.getId())
//                .name(author.getName())
//                .email(author.getEmail())
//                .password(author.getPassword())
//                .build();
//        return dto;

        /// 2. fromEntity로 조립
        // fromEntity는 아직 DTO객체가 만들어지지 않은 상태이므로, static 메서드로 설계
        AuthorDetailDto dto = AuthorDetailDto.fromEntity(author);
        return dto;
    }

}
