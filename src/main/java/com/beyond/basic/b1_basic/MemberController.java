package com.beyond.basic.b1_basic;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/*
 * [Controller 개요]
 * - @Controller가 붙은 클래스는 스프링이 객체(빈)로 생성/관리함 → 개발자가 new로 생성할 필요 없음.
 * - 요청은 "HTTP 메서드 + URL 패턴" 기준으로 적절한 컨트롤러/메서드로 라우팅(분기)됨.
 * - 컨트롤러 메서드 이름은 기능적으로 중요하지 않음 → 가독성을 위한 이름임(분기 기준은 URL/메서드).
 *
 * [요청/응답 관점]
 * - 실제로는 HTTP 응답 전체(상태코드/헤더/바디)를 내려주지만, 보통 컨트롤러에서는 주로 바디(또는 뷰 이름)만 설정함.
 * - 상태코드/헤더도 필요하면 커스텀 가능함.
 */
@Controller
@RequestMapping("/member") // "/member"로 시작하는 요청을 공통 prefix로 묶어 처리함
public class MemberController {
    /*
     * [GET 요청 응답 3가지 형태]
     * 1) text 반환 (@ResponseBody + String)
     * 2) JSON 반환 (@ResponseBody + Object) → 객체를 반환하면 자동으로 JSON 직렬화됨
     * 3) HTML(View) 반환 (@ResponseBody 없음 + String) → templates에서 뷰 이름으로 화면을 찾아 렌더링함
     *
     * [@ResponseBody]
     * - 반환값을 "데이터(텍스트/JSON)"로 응답 바디에 직접 넣어 반환할 때 사용함.
     * - 화면(HTML 템플릿)을 반환할 때는 보통 생략함(생략 시 templates에서 뷰 탐색).
     *
     * [@RestController]
     * - @Controller + @ResponseBody 조합임.
     * - 기본적으로 데이터(JSON/text) 응답 전용이라 뷰 렌더링(MVC 화면 반환) 용도로는 부적합함.
     */

    // case 1) text 응답: "홍길동" 문자열을 HTTP 응답 바디로 반환함
    @GetMapping("")
    @ResponseBody
    public String textDataReturn() {
        return "홍길동";
    }

    // case 2) JSON 응답: 객체를 반환하면 스프링이 자동으로 JSON으로 직렬화하여 반환함
    @GetMapping("/json")
    @ResponseBody
    public Member jsonDataReturn() throws JsonProcessingException {
        Member m1 = new Member("이지연", "jiyean@naver.com");

        // ObjectMapper로 직접 JSON 문자열을 만들 필요 없음.
        // 참고로 JSON 문자열(String)을 만들어서 반환하면, "객체(JSON)"가 아니라 "문자열" 응답으로 내려가게 됨.
        // ObjectMapper o1 = new ObjectMapper();
        // String data = o1.writeValueAsString(m1);
        // return data;

        return m1;
    }

    // case 3) HTML(View) 응답: @ResponseBody가 없고 String을 반환하면 뷰 이름으로 해석함
    // - templates/{뷰이름}.html(또는 템플릿 확장자) 파일을 찾아 렌더링하여 반환함
    // - Thymeleaf/JSP 등 템플릿 엔진 의존성이 필요함
    @GetMapping("/html")
    public String htmlReturn() {
        return "simple_html";
    }
}
