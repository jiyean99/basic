package com.beyond.basic.b2_board.author.service;

import com.beyond.basic.b2_board.author.domain.Author;
import com.beyond.basic.b2_board.author.dto.*;
import com.beyond.basic.b2_board.author.repository.*;
import com.beyond.basic.b2_board.post.domain.Post;
import com.beyond.basic.b2_board.post.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

/*
 * =========================================================
 * AuthorService
 * =========================================================
 * [역할]
 * - Author 관련 비즈니스 로직을 담당한다.
 * - Controller로부터 DTO를 전달받아 Domain(Entity)을 조립하고,
 *   Repository를 통해 저장/조회한 결과를 다시 DTO로 변환해 반환한다.
 *
 * [@Service]
 * - @Service는 컴포넌트 스캔 대상이며, 기본 스코프는 singleton이라 스프링 컨텍스트에서 1개의 인스턴스로 생성/관리된다.
 *
 * [@Transactional]
 * - 스프링에서 jpa를 활용할 때 트랜잭션 처리(commit, rollback)지원
 * - commit의 기준점 : 메서드 정상 종료 시점
 * - rollback의 기준점 : 예외 발생했을 경우
 * - 트랜잭션 처리가 필요없는 조회 메서드의 경우 성능향상을 위해 readOnly처리 필요
 *   조회는 커밋을 수행하지 않기 때문에 트랜잭션 처리가 불필요하다. 따라서 트랜잭션처리가 수행될 경우 성능이 저하되는 것
 *
 *
 * =========================================================
 * [실습 워크플로우]
 * =========================================================
 * (A) DI(의존성 주입) 방식 확인 실습
 *  - 필드 주입(@Autowired)
 *  - 생성자 주입(권장)
 *  - @RequiredArgsConstructor(생성자 주입 자동화)
 *
 * (B) Repository 구현체 교체 실습
 *  - AuthorMemoryRepository(in-memory)
 *  - AuthorJdbcRepository(JDBC)
 *  - AuthorMybatisRepository(MyBatis)
 *  - (추후) JPA로 교체 가능
 *
 * (C) 객체 조립 방식 확인 실습
 *  - 생성자 직접 조립
 *  - Builder 패턴 조립
 *  - toEntity / fromEntity 패턴으로 공통화
 *
 * (D) 예외 처리 흐름 확인 실습
 *  - 중복 체크: findByEmail().isPresent() -> IllegalArgumentException
 *  - 존재 체크: findById().orElseThrow() -> NoSuchElementException
 *  - 발생한 예외는 상위 계층(Controller)로 전파되고, 전역 예외 핸들러에서 공통 처리 가능
 *
 * (E) JPA 활용 시 트랜잭션 로직 추가
 *
 * =========================================================
 * TODO [DI(의존성 주입) 방법]
 * =========================================================
 * (1) 필드주입 (@Autowired)
 *  - 필드주입(@Autowired private ...)에서는 final 필드 주입이 어려워 안정성을 꾀하기 어려움(생성 이후 리플렉션 주입이라)
 *
 * (2) 생성자 주입 방식 (가장 많이 사용하는 방식이다)
 *  - 안정성 향상을 위해 final 키워드를 사용하여 상수로 사용 가능
 *    (*final은 반드시 초기화를 해줘야하기때문에 필드 주입에서는 final이 어렵지만 생성자 주입방식에선 권장하는 패턴)
 *  - 다형성 구현 가능(interface 사용 가능, 스펙만을 정의한 구조)
 *  - 순환 참조 방지(컴파일 타임에 에러 check)
 *
 * (3) Lombok @RequiredArgsConstructor 기반 생성자 주입
 *  - 반드시 초기화 되어야하는 필드(final 상수 등)를 대상으로 생성자를 자동생성해주는 어노테이션
 *  - "final(또는 @NonNull) 필드"를 파라미터로 받는 생성자를 자동 생성한다.
 *  - 위 어노테이션 선언 시 생성자 주입방식으로 의존성이 주입된다.
 *  - 다형성 설계는 불가하다
 *
 * (+) [@RequiredArgsConstructor]
 * - 초기화되지 않은 final 필드(또는 @NonNull 필드)를 대상으로 생성자를 자동 생성한다.
 * - 이 생성자가 스프링에 의해 사용되면서 “생성자 주입 방식”으로 DI가 이뤄진다.
 *
 * (+) [생성자 관련 어노테이션]
 * - NoArgus : 매개변수 없는 생성자
 * - AllArgs : 모든 필드를 초기화하는 생성자
 * - RequiredArgsConstructor : 반드시 초기화 되어야하는 필드를 초기화하는 생성자
 *
 * (+) [순환 참조 주의]
 * - S1이 S2를 필요로 하고, S2도 S1을 필요로 하면(서로 주입) 빈 생성 순서를 결정할 수 없어 순환 참조 문제가 발생한다.
 * - S1 → S2 → S3 → S1 처럼 길게 이어지는 형태도 가능하므로, 설계 단계에서 의존 관계를 끊어줘야 한다(역할 분리/별도 서비스로 분리 등).
 */

@Service
// DI(의존성) 주입 방법 - 3. RequiredArgsConstructor 어노테이션 사용
//@RequiredArgsConstructor
@Transactional
public class AuthorService {
    @Value("${aws.s3.bucket1}")
    private String bucket;

    /*
     * =========================================================
     * (A) DI 방식 확인 실습: 필드 주입 예시
     * =========================================================
     */
    /// DI(의존성) 주입 방법 - 1. Autowired (필드주입 예시)
//    @Autowired
//    private AuthorMemoryRepository authorRepository;

    /*
     * =========================================================
     * (B) Repository 교체 실습 + (A) 생성자 주입 방식 적용(현재 채택)
     * =========================================================
     * [왜 final인가]
     * - AuthorService는 AuthorRepository 없이는 동작할 수 없는 “필수 의존성”이다.
     * - final로 선언하면 생성 시점에 반드시 주입되어야 하므로(불완전한 객체 생성 방지),
     *   의존성이 누락된 상태로 서비스가 사용되는 상황을 막을 수 있다.
     *
     * [과거 실습에서 겪었던 문제(리셋 이슈)]
     * - 서비스 메서드 호출 때마다 레포지토리를 new로 생성하는 구조였다면,
     *   레포지토리 내부의 in-memory 데이터(List) 등이 매번 초기 상태로 돌아가
     *   “저장했는데 다음 호출에서 조회가 안 되는” 문제가 생길 수 있었다.
     *
     * [현재 구조(임시 해결 → DI 적용)]
     * - 현재는 스프링 DI로 Repository를 주입받아 재사용하는 구조로 변경했다.
     * - 즉 매번 new로 만들지 않고, 스프링이 관리하는 인스턴스를 주입받아 사용한다.
     *
     * [Repository 교체 실습 포인트]
     * - 아래처럼 "필드 타입/생성자 파라미터 타입"만 바꾸면
     *   Service 로직은 크게 수정하지 않고 Repository 구현체를 교체할 수 있다.
     *   (in-Memory <-> JDBC <-> MyBatis <-> JPA)
     *
     * [추가 정리]
     * - 진짜 다형성 설계를 하려면, 필드 타입을 interface로 두고 구현체를 바꿔끼우는 게 가장 깔끔함
     *   (현재 코드는 "구현체 타입 교체 실습"을 주석으로 남겨둔 상태)
     */

    /// DI(의존성) 주입 방법 - 2. 생성자 주입 방식
    /// 현재 생성자 주입방식으로 작성하였기에 다형성 설계가 가능하므로,

    // TODO [in-memory 레파지토리 사용 예시]
//    private final AuthorMemoryRepository authorRepository;

    // TODO [jdbc 연결 레파지토리 사용 예시]
//    private final AuthorJdbcRepository authorRepository;

    // TODO [mybatis 연결 레파지토리 사용 예시]
//    private final AuthorMybatisRepository authorRepository;

    // TODO [jpa 연결 레파지토리 사용 예시]
//    private final AuthorJpaRepository authorRepository;

    // TODO [spring data jpa 연결 레파지토리 사용 예시]
    private final AuthorRepository authorRepository;
    private final PostRepository postRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Client s3Client;

    @Autowired
    public AuthorService(AuthorRepository authorRepository, PostRepository postRepository, PasswordEncoder passwordEncoder, S3Client s3Client) {
        // 해당 생성자를 주입받겠다는 의미로 해당 어노테이션 추가(생성자가 하나밖에 없을 때에는 생략 가능)
        this.authorRepository = authorRepository;
        this.postRepository = postRepository;
        this.passwordEncoder = passwordEncoder;
        this.s3Client = s3Client;
    }

    /*
     * =========================================================
     * (A) DI 방식 확인 실습: @RequiredArgsConstructor 예시
     * =========================================================
     * - 아래처럼 @RequiredArgsConstructor를 켜면, final 필드 대상으로 생성자가 자동 생성됨
     * - 다형성 설계를 하려면 "필드 타입을 interface"로 두면 된다.
     */

    /// DI(의존성) 주입 방법 - 3. RequiredArgsConstructor 어노테이션 사용
    // private final AuthorMemoryRepository authorRepository;

    /*
     * =========================================================
     * (C) 객체 조립 방식 확인 실습
     * =========================================================
     * [객체 직접 조립]
     * (1) 생성자만을 활용한 객체 조립
     * - id를 사용하지 않고싶다면 생성자 오바라이딩을 해야한다. 매번 생성자 오버라이딩을 하면 너무 비효율적임
     * (2) Builder 패턴을 활용하여 객체 조립
     * - 매개변수 개수의 유연성
     * - 매개변수 순서의 유연성
     * - 빌더패턴은 AllArgs 생성자 기반으로 동작
     *
     * [toEntity, fromEntity 패턴을 통한 객체 조립]
     * - 객체 조립이라는 반복적인 작업을 별도의 코드로 떼어내 공통화하는 작업
     * - DTO에서 공통화 작업을 수행함
     * - fromEntity는 아직 DTO객체가 만들어지지 않은 상태이므로, static 메서드로 설계
     *
     * =========================================================
     * (D) 메서드별 워크플로우(실제 서비스 로직 흐름)
     * =========================================================
     * save: dto -> entity 조립 -> 중복 검증 -> 저장
     * findAll: 전체 조회 -> entity list -> dto list 변환
     * findById: 단건 조회(Optional) -> 없으면 예외 -> dto 변환
     * delete: 단건 조회(Optional) -> 없으면 예외 -> 삭제
     */
    public void save(AuthorCreateDto dto, MultipartFile profileImg) {
        /*
         * [save 워크플로우]
         * 1) 입력(dto) -> entity 조립
         * 2) 정책 체크(이메일 중복) -> 예외 처리
         * 3) repository 저장
         */

        /// 1-1. 생성자만을 활용한 객체 조립
        // Author author = new Author(null, dto.getName(), dto.getEmail(), dto.getPassword());

        /// 1-2. Builder 패턴 사용하여 조립 - Author 도메인 내 어노테이션 추가
//        Author author = Author.builder()
//                .email(dto.getEmail())
//                .name(dto.getName())
//                .password(dto.getPassword())
//                .build();

        /// 2. toEntity로 조립
        // Author author = dto.toEntity();

        /// 예외처리 로직 추가
        /// - 기존 코드에서 dto.toEntity()를 2번 호출하던 부분은, 워크플로우 파악을 위해 한 번만 조립하도록 개선

        // TODO 암호화하지 않은 dto에 들어오고 이를 암호화 해서 author 객체로 만들어줘야함
        Author author = dto.toEntity(passwordEncoder.encode(dto.getPassword()));
        if (authorRepository.findByEmail(author.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        // [CascadeType.PERSIST 옵션 예시를 위한 코드]
        author.getPostList().add(Post.builder().title("안녕하세요").author(author).build()); // 이 때 반드시 List를 초기화해줘야함
        authorRepository.save(author);

        // [Cascade 옵션이 적용되지 않은 예시를 위한 코드]
        // authorDB -> 쓰기지연이 발생하여서 영속성 컨텍스트에 모아놓고 쿼리를 짜주니 저장되기 전임에도 해당 객체를 사용할 수 있게 됨

        // Author authorDB = authorRepository.save(author);
        // postRepository.save(Post.builder().title("안녕하세요").author(authorDB).build());

        // 예외 발생 시 @Transactional 어노테이션에 의해 rollback 처리
        // authorRepository.findById(10L).orElseThrow(() -> new NoSuchElementException("롤백 테스트를 위한 코드입니다."));

        //TODO S3 관련 템플릿 코드
        if (profileImg != null){
            // 파일 업로드를 위한 저장객체 구성 : s3Client.putObject(저장객체, 이미지)
            String fileName = "uesr-" + author.getId() + "-profileimage-" + profileImg.getOriginalFilename();
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName) // 파일명
                    .contentType(profileImg.getContentType()) // images/jpeg, video/mp4 등의 컨텐츠 타입 정보
                    .build();
            /// (1) AWS에 이미지 업로드(byte 형태로 변환해서 업로드)
            try {
                s3Client.putObject(request, RequestBody.fromBytes(profileImg.getBytes()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            /// (2) AWS의 이미지 URL 추출
            String imgUrl = s3Client.utilities().getUrl(a->a.bucket(bucket).key(fileName)).toExternalForm();

            // 외부와의 통신이기때문에 영속성컨텍스트가 실행되지 않을 수 있다는 우려가 있어 코드의 위치를 하단으로 옯기고,
            // 이에 따라 프로필이미지 데이터를 별도로 업데이트 처리 수행
            author.updateProfileImageUrl(imgUrl);
        }

    }

    public Author login(AuthorLoginDto dto) {
        // 아래 구조의 보안 취약점 : 이메일이 잘못되었는지, 비밀번호가 잘못되었는지 알려주는것은 특정 데이터가 맞았다 라고 알 수 있기 때문에 특정해서 알려주면 안됨
        /*
        Author author = authorRepository.findAllByEmail(dto.getEmail()).orElseThrow(() -> new IllegalArgumentException("일치하는 이메일이 없습니다."));
        if (!passwordEncoder.matches(dto.getPassword(), author.get().getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        return author;
        */

        Optional<Author> optionalAuthor = authorRepository.findByEmail(dto.getEmail());

        boolean check = true;

        if (!optionalAuthor.isPresent()) {
            check = false;
        } else {
            if (!passwordEncoder.matches(dto.getPassword(), optionalAuthor.get().getPassword())) {
                check = false;
            }
        }

        if (!check) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }

        return optionalAuthor.get();
    }

    @Transactional(readOnly = true)
    public List<AuthorListDto> findAll() {
        /*
         * [findAll 워크플로우]
         * 1) repository에서 전체 조회
         * 2) entity -> dto(fromEntity) 변환
         * 3) List<DTO> 반환
         */

        /// [기존 방식]
//        List<Author> authorList = authorRepository.findAll();
//        List<AuthorListDto> authorListDtoList = new ArrayList<>();
//        for (Author a : authorList) {
//            AuthorListDto dto = new AuthorListDto(a.getId(), a.getName(), a.getEmail());
//            authorListDtoList.add(dto);
//        }
//        return authorListDtoList;

        /// [fromEntity 방식으로 객체 조립 방식]
//        List<Author> authorList = authorRepository.findAll();
//        List<AuthorListDto> authorListDtoList = new ArrayList<>();
//        for (Author author: authorList){
//            AuthorListDto dto = AuthorListDto.fromEntity(author);
//            authorListDtoList.add(dto);
//        }
//        return authorListDtoList;

        /// [fromEntity 방식+stream api]
        return authorRepository
                .findAll()
                .stream()
                .map(a -> AuthorListDto.fromEntity(a))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AuthorDetailDto findById(Long id) {
        /*
         * [findById 워크플로우]
         * 1) repository에서 id로 조회(Optional)
         * 2) 없으면 orElseThrow로 예외 발생(Service 정책)
         * 3) entity -> detail dto(fromEntity) 변환
         */

        /// 1. Repository에서 Optional로 조회
        Optional<Author> optionalAuthor = authorRepository.findById(id);

        /// 2. 값이 없으면 예외 발생 (Service 정책)
        Author author = optionalAuthor.orElseThrow(
                () -> new NoSuchElementException("Entity is not found")
        );
        // NOTE: "JPA로 바꾸면 자동으로 Entity 없음 예외가 난다"기보단,
        // findById는 JPA에서도 Optional을 반환하는 패턴이라 보통 Service에서 orElseThrow로 정책을 정한다.

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

        // =========================================================
        // [기존 방식] postCount를 Service에서 계산해서 DTO로 전달
        // - 글 개수를 구하기 위해 Post 목록을 직접 조회
        // - delYn = "NO" 조건으로 soft delete 글 제외 가능
        // - 단점: 개수만 필요한데 List<Post>를 통째로 조회할 수 있어 비효율 가능
        // =========================================================
        // [기존코드]
        // List<Post> postList = postRepository.findAllByAuthorIdAndDelYn(author.getId(), "NO");
        // return AuthorDetailDto.fromEntity(author, 0);

        // =========================================================
        // [변경 방식] postCount 계산을 DTO 내부로 이동
        // - Service는 author만 조회하고 DTO 변환만 수행(로직 단순화)
        // - 단점: author.getPostList().size() 호출 시 LAZY 로딩이면 추가 쿼리 발생 가능
        // - 단점: 현재 구현은 soft delete(delYn="YES")까지 포함될 수 있음(정확한 "미삭제 글 수"와 다를 수 있음)
        // =========================================================
        // [변경코드]
        return AuthorDetailDto.fromEntity(author);
    }

    // 인증객체를 서비스 계층에서 가져오는 방식
    /*
    @Transactional(readOnly = true)
    public AuthorDetailDto myInfo1(){
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        Optional<Author> optionalAuthor = authorRepository.findByEmail(email);

        Author author = optionalAuthor.orElseThrow(
                () -> new EntityNotFoundException("Entity is not found")
        );
        return AuthorDetailDto.fromEntity(author);
    }
    */

    // 인증객체를 컨트롤러에서 매개변수로 받아오는 방식
    @Transactional(readOnly = true)
    public AuthorDetailDto myInfo(String principal) {
        Optional<Author> optionalAuthor = authorRepository.findByEmail(principal);

        Author author = optionalAuthor.orElseThrow(
                () -> new EntityNotFoundException("Entity is not found")
        );
        return AuthorDetailDto.fromEntity(author);
    }

    public void delete(Long id) {
        /*
         * [delete 워크플로우]
         * 1) 존재 여부 확인(findById -> orElseThrow)
         * 2) delete 수행
         */

        // cf) DB에서 조회작업과 삭제작업 중 무엇의 부하가 더 클까? 삭제작업이 더 자원이 소모됨(조회작업이 더 가벼움)

        /// 방법1 : 레파지토리에 바로 delete 수행하기
        // - 에러가 발생하면 DB에 부하가 간다.(에러는 서버에도 전파가 됨)

        /// 방법2: 데이터 조회 후 없다면 예외 처리 -> delete 수행 (2step)
        // - DB에서는 에러가 발생하지 않게 됨
        // - 존재하지 않는 id 삭제 요청을 명확하게 예외로 처리하기 위함
        Author author = authorRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Entity is not found"));

        // [기존] - id값으로 삭제작업 수행
//        authorRepository.delete(id);
        // [변경] - JPA의 경우 객체지향이므로 author 객체를 주입시켜서 삭제 수행
        authorRepository.delete(author);
    }

    public Author updatePassword(AuthorUpdatePwDto dto) {
        Author author = authorRepository.findByEmail(dto.getEmail()).orElseThrow(() -> new NoSuchElementException("해당 메일로 가입된 계정이 없습니다."));
        author.updatePassword(dto.getPassword());

        // insert, update 모두 save 메서드 사용 -> 변경감지로 대체
//        return authorRepository.save(author);
        /*
         * [영속성 컨텍스트]
         * - 애플리케이션과 DB 사이에서 객체를 보관하는 가상의 DB 역할
         *
         * [주요 기능]
         * (1) 쓰기 지연: insert, update 등의 작업사항을 즉시 실행하지 않고, 커밋 시점에 모아서 실행(성능향상)
         * (2) 변경 감지(dirty checking) : 영속상태/관리상태(managed)의 엔티티는 트랜잭션 커밋 시점에 변경감지를 통해 별도의 save 없이 DB에 반영
         *       * 관리상태가 되려면 조회를 해야함
         *       * 결국 update는 save하지 않아도 되게 됨
         * */
        return author;
    }
}
