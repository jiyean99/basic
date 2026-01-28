package com.beyond.basic.b3_suvlet;

import com.beyond.basic.b1_basic.Member;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.PrintWriter;

/*
 * [Servlet, 서블릿]
 * - 사용자의 http 요청을 쉽게 처리하고, 사용자에게 http 응답을 쉽게 조립해주는 기술
 * - 서블릿에서는 url 매핑을 메서드단위가 아닌, 클래스 단위로 지정
 * */
@Slf4j
@WebServlet("/servlet/get")
public class ServletReqGet extends HttpServlet {
    private final ObjectMapper objectMapper;

    @Autowired
    public ServletReqGet(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // 메서드 오버라이드
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 사용자의 요청 : http://localhost:8080/servlet/get?id=1
        // 사용자에게 줄 응답 : JSON 객체 형식
        String id = req.getParameter("id");
        log.info(id);
        Member member = new Member("hong1", "hong1@naver.com");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter printWriter = resp.getWriter();
        printWriter.print(objectMapper.writeValueAsString(member));
        printWriter.flush();
    }
}
