package com.beyond.basic.b2_board.author.controller;

import com.beyond.basic.b2_board.author.dto.AuthorDetailDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/*
 * ResponseEntity 연습용 컨트롤러.
 * - 실습 목적용이라 실제 프로젝트 전체 구성에는 필수적이지 않음.
 *
 * (1) @ResponseStatus
 *   - 메서드에 직접 HTTP 상태 코드를 지정할 때 사용.
 *   - 간단한 경우에 유용하지만, 상황에 따라 상태 코드를 동적으로 바꾸기에는 적합하지 않다.
 *
 * (2) ResponseEntity
 *   - HTTP 응답의 Body뿐 아니라, 상태 코드와 Header까지 함께 제어할 수 있는 클래스
 *   - 동적인 상태 코드/헤더 설정이 필요할 때 주로 사용.
 */
@RestController
@RequestMapping("/response_entity")
public class ResponseEntityController {

    // 1. @ResponseStatus 사용 예시
    @ResponseStatus(HttpStatus.CREATED)
    @GetMapping("/annotation")
    public String annotation() {
        return "OK";
    }

    // 2-1. ResponseEntity 사용 예시 (기본 생성자 방식)
    @GetMapping("/method1")
    public ResponseEntity<String> method1() {
        return new ResponseEntity<>("OK", HttpStatus.CREATED);
    }

    // 2-2. ResponseEntity 사용 예시 (제네릭을 와일드카드로)
    // - 반환 타입을 ?로 두어 다양한 타입으로 분기 처리할 수 있다.
    @GetMapping("/method2")
    public ResponseEntity<?> method2() {
        return new ResponseEntity<>("OK", HttpStatus.CREATED);
    }

    // 2-3. ResponseEntity 체이닝 방식 예시
    // - 빌더 스타일 API로 상태 코드, 헤더, 바디를 직관적으로 설정할 수 있다.
    // - 실무에서 가장 많이 사용하는 방식
    @GetMapping("/method3")
    public ResponseEntity<?> method3() {
        AuthorDetailDto dto = AuthorDetailDto.builder()
                .id(1L)
                .name("test")
                .email("test@naver.com")
                .password("test")
                .build();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Content-Type", "application/json")  // 보통 기본값을 그대로 사용하는 편입니다.
                .body(dto);
    }

    // 2-4. ResponseEntity 체이닝 방식의 간단 예시
    // - ok(…)를 사용하면 상태 코드는 200 OK로 고정됩니다.
    @GetMapping("/method4")
    public ResponseEntity<?> method4() {
        AuthorDetailDto dto = AuthorDetailDto.builder()
                .id(1L)
                .name("test")
                .email("test@naver.com")
                .password("test")
                .build();

        return ResponseEntity.ok(dto);
    }

}
