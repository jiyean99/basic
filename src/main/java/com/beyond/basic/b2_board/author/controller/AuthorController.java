package com.beyond.basic.b2_board.author.controller;

import com.beyond.basic.b2_board.author.domain.Author;
import com.beyond.basic.b2_board.author.dto.*;
import com.beyond.basic.b2_board.author.service.AuthorService;
import com.beyond.basic.b2_board.common.auth.JwtTokenProvider;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 * =========================================================
 * AuthorController
 * =========================================================
 *
 * [역할]
 * - Author 관련 HTTP 요청을 받아 Service에 위임하고, Service에서 반환한 DTO를 그대로 응답으로 내려준다.
 *
 * TODO [@RestController]
 * - @Controller + @ResponseBody 조합으로, 메서드 반환값을 뷰 이름이 아닌 HTTP 응답 바디(JSON/text)로 바로 내려준다.
 *
 * TODO [@RequestMapping("/author")]
 * - "/author" prefix 담당(이 컨트롤러의 모든 API 공통 경로)
 *
 * =========================================================
 * [실습 워크플로우 변화]
 * =========================================================
 *
 * (기존) Controller에서 try-catch로 예외를 잡고 CommonErrorDto를 직접 생성해서 리턴
 * (개선) Controller는 정상 흐름만 담당하고,
 *       예외는 전역 예외 핸들러(@RestControllerAdvice)에서 공통 처리한다. [web:52][web:45]
 */
@RestController
@RequestMapping("/author")
public class AuthorController {

    /*
     * =========================================================
     * 의존성: AuthorService
     * =========================================================
     *
     * [과거 구조에서의 문제]
     * - 컨트롤러 내부에서 AuthorService를 매번 new로 생성하거나,
     *   기본 생성자에서 직접 new AuthorService()로 생성하는 방식이었을 경우,
     *   서비스/레포지토리의 in-memory 데이터가 호출마다 초기화될 수 있었다.
     *
     * [현재 구조]
     * - 스프링 DI를 통해 AuthorService를 주입받는 구조로 변경했다.
     * - AuthorService는 @Service로 싱글톤 빈으로 관리되며, 컨트롤러는 그 인스턴스를 주입받아 재사용한다.
     *
     * [생성자 주입 사용 이유]
     * - final로 선언해 “필수 의존성”임을 명시하고, 생성 시점에 반드시 주입되도록 강제할 수 있다.
     * - 불변성 확보 + 테스트/리팩터링 시에도 유리하다.
     */
    private final AuthorService authorService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public AuthorController(AuthorService authorService, JwtTokenProvider jwtTokenProvider) {
        this.authorService = authorService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /*
     * =========================================================
     * 1. 회원가입(Create)
     * =========================================================
     *
     * [URL] POST /author/create
     * [요청] {"name": "lee", "email": "lee@naver.com", "password": "1234"}
     * [설명] AuthorCreateDto로 바인딩 → Service 저장 위임 → "OK" 반환
     */
    // email 중복 시 에러발생 -> Illegal 400
    @PostMapping("/create")
    // dto에 있는 validation 어노테이션과 Valid 한 쌍
    public ResponseEntity<?> create(@RequestBody @Valid AuthorCreateDto dto) {
        /// 아래 예외처리는 Exception Handler에서 전역적으로 예외처리하고있다.
        /// (기존 try-catch 예외처리 실습 코드는 "학습 흔적"으로 주석 보존)
        /// - 전역 예외 핸들러 도입 후에는 Controller가 try-catch를 가질 필요가 없음
//        try {
//            authorService.save(dto);
//            return ResponseEntity.status(HttpStatus.CREATED).body("OK");
//        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//            CommonErrorDto commonErrorDto = CommonErrorDto.builder()
//                    .status_code(400)
//                    .error_message(e.getMessage())
//                    .build();
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(commonErrorDto);
//        }

        authorService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("OK");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthorLoginDto dto) {
        Author author = authorService.login(dto);
        // 토큰 생성 및 return
        String token = jwtTokenProvider.createToken(author);

        return ResponseEntity.status(HttpStatus.OK).body(token);
    }

    /*
     * =========================================================
     * 2. 회원 목록 조회(Read - List)
     * =========================================================
     *
     * [URL] GET /author/list
     * [응답] [{"id":1,"name":"lee","email":"lee@naver.com"}, ...]
     * [설명] Service에서 목록 DTO를 받아 그대로 반환(민감정보 제외)
     */
    @GetMapping("/list")
    // [@PreAuthorize] : Authentication 객체 안의 권한정보를 확인하는 어노테이션, 이 때 예외는 filter계층에서 확인하므로 filter계층에서 인가에러가 발생하는 것
    // 두 개 이상의 Role을 허용하는 경우:
    // ex) @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
    @PreAuthorize("hasRole('ADMIN')")
    public List<AuthorListDto> findAll() {
        return authorService.findAll();
    }

    /*
     * =========================================================
     * 3. 회원 상세 조회(Read - Detail)
     * =========================================================
     *
     * [URL] GET /author/{id} (ex: /author/1)
     * [설명] PathVariable id로 조회 → AuthorDetailDto 반환
     */

    // TODO [기존코드]
    // 문제점 : http 응답 body를 분기처리한다고 하더라도, 상태코드는 200으로 고정되어있다.
//    @GetMapping("/{id}")
//    // AuthorDetailDto -> Object : 에러메시지를 return할 수 있도록 조상클래스인 Object로 변경
//    // CommonErrorDto : body에 에러에 대한 내용을 전달
//    public Object findById(@PathVariable Long id) {
//        try {
//            AuthorDetailDto dto = authorService.findById(id);
//            return dto;
//        } catch (NoSuchElementException e) {
//            e.printStackTrace();
//            return CommonErrorDto.builder()
//                    .status_code(404)
//                    .error_message(e.getMessage())
//                    .build();
//        }
//    }

    /*
     * =========================================================
     * [개선 포인트]
     * - Controller에서 try-catch로 ResponseEntity 분기하던 작업을
     *   전역 예외 핸들러로 이동(@RestControllerAdvice) [web:52][web:45]
     * - Controller는 성공 응답(200)만 내려주고, 실패 응답은 Advice에서 내려준다.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {

        // [ResponseEntity+try-catch 방식 실습 코드]
//        try {
//            AuthorDetailDto dto = authorService.findById(id);
//            return ResponseEntity.status(HttpStatus.OK).body(dto);
//        } catch (NoSuchElementException e) {
//            e.printStackTrace();
//            CommonErrorDto dto = CommonErrorDto.builder()
//                    .status_code(404)
//                    .error_message(e.getMessage())
//                    .build();
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(dto);
//        }

        AuthorDetailDto dto = authorService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    /*
     * =========================================================
     * 4. 회원 탈퇴(Delete)
     * =========================================================
     *
     * [URL] DELETE /author/{id} (ex: /author/1)
     * [설명] Service에 삭제 위임 후 "OK" 반환
     */
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        authorService.delete(id);
        return "OK";
    }

    /*
     * =========================================================
     * 5. 비밀번호 수정(Password Patch)
     * =========================================================
     *
     * [URL] PATCH /author/passwordPatch
     *
     * [설명]
     * - PathVariable로 id를 받아 Service에서 조회한 AuthorDetailDto를 반환한다.
     * -
     * - AuthorUpdatePwDto : 쿼리파라미터로 email를 받아 Service에서 email로 객체 조회 후 password 변경한다.
     * - Repository에서는 save로 처리하면 update가 된다.
     */
    @PatchMapping("/updatePassword")
    public ResponseEntity<?> updatePassword(@RequestBody AuthorUpdatePwDto dto) {
        Author author = authorService.updatePassword(dto);
        return ResponseEntity.status(HttpStatus.OK).body(author);
    }
}
