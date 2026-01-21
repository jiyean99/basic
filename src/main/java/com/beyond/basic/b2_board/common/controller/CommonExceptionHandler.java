package com.beyond.basic.b2_board.common.controller;

import com.beyond.basic.b2_board.common.dto.CommonErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/*
 * TODO [@RestControllerAdvice]
 * - @Controller 어노테이션이 붙어있는 모든 클래스의 예외를 인터셉팅(가로채기)
 * */
//MethodArgumentNotValidException
@RestControllerAdvice
public class CommonExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> illegalArgumentException(IllegalArgumentException e) { // 에러 주입
        e.printStackTrace();
        CommonErrorDto dto = CommonErrorDto.builder()
                .status_code(400)
                .error_message(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(dto);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        e.printStackTrace();
        CommonErrorDto dto = CommonErrorDto.builder()
                .status_code(400)
                .error_message(e.getFieldError().getDefaultMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(dto);
    }

    @ExceptionHandler(NoSuchMethodException.class)
    public ResponseEntity<?> noSuchMethodException(NoSuchMethodException e) {
        e.printStackTrace();
        CommonErrorDto dto = CommonErrorDto.builder()
                .status_code(404)
                .error_message(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(dto);
    }

    // 예외처리와 마찬가지로 구체적인 예외가 우선해서 실행되고 그 외 예외를 Exception
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonErrorDto exception(Exception e) {
        e.printStackTrace();
        return CommonErrorDto.builder()
                .status_code(500)
                .error_message(e.getMessage())
                .build();
    }
}
