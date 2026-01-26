package com.beyond.basic.b2_board.common.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectMapperConfig {
    // 스프링 전역에서 주입받아 사용할 수 있도록 ObjectMapper를 Bean으로 등록
    //
    // ObjectMapper는 JSON 직렬화/역직렬화에 사용되며, 프로젝트 전반(모든 Controller 등)에서 공통으로 활용된다.
    // 다만 날짜/시간 포맷, 특정 JSON 규칙 등 “전역 커스텀”이 필요한 경우가 있어 설정을 추가할 수 있는데,
    // 이 객체를 전역으로만 커스텀해서 쓰면(= 모든 곳에 영향) 요구사항이 섞일 때 리스키할 수 있다.
    //
    // ex) objectMapper.registerModule() 등을 통한 시간 커스텀
    //
    // 정리:
    // 1. ObjectMapper 객체는 스프링에서 기본적으로 싱글통 객체로 생성
    // 2. 해당 객체는 모든 Controller 등에서 사용되고 있으며,
    //    아래와 같이 별도의 커스텀이 가능하다.
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper;
    }
}
