package com.beyond.basic.b2_board.common.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.RequestContext;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/*
 * [@Aspect]
 * - AOP 코드임을 명시
 *
 * [@Pointcut]
 * - AOP의 대상이 되는 Controller, Service 등을 명시
 *
 * [join point]
 * - join point는 사용자가 실행하고자 하는 코드를 의미하고, 위에서 정의한 point cut을 의미
 * */
@Aspect
@Component
@Slf4j
public class AopLogService {
    // TODO 작업 순서 :
    //  낚아채려는 대상 탐색(PointCut) -> 공통성 코드 작성(Around: join point before -> join point -> join point after)

    // (1) 어노테이션을 기준으로 명시
    // 해석: RestController 어노테이션이 붙어있는 모든 컨트롤러(앞의 주소는 해당 어노테이션의 패키지 구조)
    // 어노테이션을 기준으로 잡으면 매우 경직적이게 됨
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerPointCut() {
    }

    // (2) 패키지 기준으로 명시
    /*
    @Pointcut("within(com.beyond.basic.b2_board.author.controller.AuthorController)")
    public void controllerPointCut1() {
    }
    */

    /// AOP 활용방법 (1) : Around 어노테이션을 통해 before, join point, after 코드를 한번에 작성하는 방식
//    @Around("controllerPointCut()")
//    public Object controllerLogger(ProceedingJoinPoint joinPoint) {
//        // join point before
//        log.info("====== Around start ======");
//        log.info("aop start");
//        log.info("요청자 : " + SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
//        log.info("요청 메서드명 : " + joinPoint.getSignature().getName());
//
//        // servlet 계층에서 HTTP 요청을 꺼내는 방법
//        // 이 때 body의 경우 일회성 객체이므로 잘 안꺼낸다.
//        ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
//        HttpServletRequest request = attributes.getRequest();
//        log.info("HTTP URI : " + request.getRequestURI());
//        log.info("HTTP method : " + request.getMethod());
//        log.info("HTTP Header - Authorization(Token) : " + request.getHeader("Authorization"));
//        log.info("HTTP Header - Content-Type : " + request.getHeader("Content-Type"));
//
//        // join point run
//        Object object = null;
//        try {
//            object = joinPoint.proceed();
//        } catch (Throwable e) {
//            throw new RuntimeException(e);
//        }
//
//        // join point after
//        log.info("aop end");
//        log.info("====== Around end ======");
//        return object;
//    }

    /// AOP 활용방법 (2) : Before, After 어노테이션을 사용하여 각각 따로 작성하는 방식
//    @Before("controllerPointCut()")
//    public void beforeControllerLogger(JoinPoint joinPoint) {
//        log.info("====== Before start ======");
//        log.info("aop start");
//        log.info("요청자 : " + SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
//        log.info("요청 메서드명 : " + joinPoint.getSignature().getName());
//        log.info("====== Before end ======");
//    }
//    @After("controllerPointCut()")
//    public void afterControllerLogger(){
//        log.info("====== After start ======");
//        log.info("aop end");
//        log.info("====== After end ======");
//    }
}

