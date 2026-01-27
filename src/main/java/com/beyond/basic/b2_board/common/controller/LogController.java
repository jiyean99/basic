package com.beyond.basic.b2_board.common.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * [Log 관리]
 * - System.out.println의 문제점
 *   (1) 출력의 성능이 떨어짐
 *   (2) 로그 분류작업 불가
 * - logback 라이브러리 사용 (가장 많이 사용되는 로그 라이브러리)
 *
 * [logback 객체 생성 방법]
 * (1) Logger static 객체 생성 (slf4j 라이브러리)
 * (2) @Slf4j 어노테이션 선언
 *   Slf4j 어노테이션 선언시, log 라는 변수로 객체 사용 가능
 * */
@Slf4j
@RestController
public class LogController {
    private static Logger logger = LoggerFactory.getLogger(LogController.class);

    @GetMapping("/log/test")
    public String logTest() {
        // [sout 사용]
        //System.out.println("hello world");

        // [logback 사용 1 - 객체 직접 생성]
        logger.trace("trace 로그 입니다.");
        logger.debug("debug 로그 입니다.");
        logger.info("info 로그 입니다.");
        logger.error("error 로그 입니다.");

        // [logback 사용 2 - Slf4j 사용하여 객체 생성]
        log.trace("Slf4j 테스트 : strace 로그 입니다.");
        log.debug("Slf4j 테스트 : debug 로그 입니다.");
        log.info("Slf4j 테스트 : info 로그 입니다.");
        log.error("Slf4j 테스트 : error 로그 입니다.");

        // [실전 사용 예시]
        try {
            // 1. info레벨 혹은 debug 레벨로 에러 로그 시작 찍기
            log.info("에러 테스트 시작");
            throw new IllegalArgumentException("에러 테스트");
        } catch (IllegalArgumentException e) {
            // printStackTrace는 성능 문제로 사용 지양
            // e.printStackTrace();

            // 2. error 레벨의 로그 찍기
            log.error("에러 메시지 === ", e);
        }
        return "OK";
    }
}
