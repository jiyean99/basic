package com.beyond.basic.b2_board.common.exception;

import com.beyond.basic.b2_board.common.dto.CommonErrorDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

@Component
public class JwtAuthenticationHandler implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;
    @Autowired
    public JwtAuthenticationHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // Start Line + Header 조립
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 상태코드 세팅
        response.setContentType("application/json"); // body에 ErrorDTO 형태로 리턴해주기 위해 컨텐츠타입 json으로 세팅
        response.setCharacterEncoding("UTF-8");

        // Body 조립
        // 객체 -> 직렬화
        String data = objectMapper.writeValueAsString(CommonErrorDto.builder()
                .status_code(401).error_message("토큰이 없거나 유효하지 않습니다.").build());
        PrintWriter printWriter = response.getWriter();
        printWriter.write(data);
        printWriter.flush();
    }
}
