package com.beyond.basic.b2_board.author.controller;

import com.beyond.basic.b2_board.author.dto.AuthorDetailDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// ResponseEntity 실습목적의 컨트롤러로 프로젝트 전체 구성에는 필요 없음(패턴별로 학습할 필요는 X)

/*
 * (1) TODO [@ResponseStatus 어노테이션 사용]
 * - 메서드 상단에 붙이며 상황에 따른 분기처리가 어려움
 * (2) TODO [ResponseEntity]
 * - HTTP 응답 객체의 body뿐만 아니라, 상태코드 및 헤더요소를 바꿔야 하는 경우에 사용하는 클래스
 * */
@RestController
@RequestMapping("/response_entity")
public class ResponseEntityController {
    /// 1. @ResponseStatus 사용
    @ResponseStatus(HttpStatus.CREATED)
    @GetMapping("/annotation")
    public String annotation() {
        return "OK";
    }

    /// 2-1. ResponseEntity 방식
    @GetMapping("/method1")
    public ResponseEntity<String> method1() {
        return new ResponseEntity<>("OK", HttpStatus.CREATED);
    }

    /// 2-2. ResponseEntity 방식
    // - ResponseEntity의 자료를 ?로 분기처리가 가능하도록 활용
    @GetMapping("/method2")
    public ResponseEntity<?> method2() {
        return new ResponseEntity<>("OK", HttpStatus.CREATED);
    }

    /// 2-3. ResponseEntity 방식 - 체이닝방식
    // - ResponseEntity, ?(any), 빌더패턴을 사용하여 상태코드, Header, Body를 쉽게 생성
    // - 가장 추천하는 방식
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
                .header("Content-Type", "application/json") // 거의 수정하지 X
                .body(dto);
    }

    /// 2-4. ResponseEntity 방식 - 체이닝방식
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
