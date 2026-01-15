package com.beyond.basic.b1_basic;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    // case 3-1) 정적인 HTML(View) 응답
    // - @ResponseBody가 없고 String을 반환하면 뷰 이름으로 해석함
    // - templates/{뷰이름}.html(또는 템플릿 확장자) 파일을 찾아 렌더링하여 반환함
    // - Thymeleaf/JSP 등 템플릿 엔진 의존성이 필요함
    @GetMapping("/html")
    public String htmlReturn() {
        return "simple_html";
    }

    // case 3-2) 동적인 HTML(View) 응답: 서버에서 화면 + 데이터를 함께 주는 동적인 화면
    // - 아래의 케이스는 SSR(Server Side Rendering)방식이고, CSR(Client Side Rendering) 방식은 화면은 별도제공하고 서버는 데이터만 제공한다.
    // - 이 때 화면 렌더링 속도는 SSR 방식이 더 빠르다.
    @GetMapping("/html/dynamic")
    public String htmlForDynamicReturn(Model model) {
        // model 객체가 위 데이터를 화면에 전달해준다. 어떻게? 스프링의 의존성 중 타임리프가 해주는 것(화면에 자바의 변수를 넘겨주는것이 핵심 기술이다)
        // model 객체는 데이터를 화면에 전달해주는 역할
        // name = 김아무개 형태로 화면에 전달
        model.addAttribute("name", "김아무개");
        model.addAttribute("email", "kim@naver.com");
        return "dynamic_html";
    }


    /*
     * [GET 요청 URL 데이터 추출 방식 2가지]
     * 1) path variable 패턴
     * 2) 쿼리 파라미터 패턴
     *
     * [@PathVariable]
     * - 별도의 형 변환 없이 원하는 자료형으로 형변환되어 매개변수로 주입시켜줌
     * - 매개변수의 변수명은 반드시 URL의 변수명과 일치해야함
     *
     * [@RequestParam]
     * - 파라미터
     * - value에 key값을 넣어줘야한다.
     *
     * [@ModelAttribute]
     * - form 데이터 형식(파라미터 방식)의 데이터를 객체로 파싱하는데 사용하는 어노테이션
     * - 파라미터가 많아지는 경우 데이터 바인딩을 해줌.
     * - 데이터 바인딩 : 파라미터의 데이터를 모아 객체로 자동 매핑 및 생성
     * - 클래스를 기반으로 객체를 생성하려면 기본생성자와 getter를
     * - 위 어노테이션은 생략 가능하다.
     *
     * [@RequestBody]
     * - JSON 데이터를 객체로 파싱하는데 사용하는 어노테이션
     *
     * [@RequestPart]
     * -
     */

    // case 1) path variable 방식을 통해 사용자로부터 URL에서 데이터 추출
    // - 데이터의 형식 : /member/path/1
    @GetMapping("/path/{id}")
    @ResponseBody
    // 들어온 URL 데이터의 타입은 String이였을것이다. 이 때 PathVariable 어노테이션을 사용하여 내가 지정한
    // Long으로 형변환을 해주게 되는 것
    // 이 때 Wrapper class를 사용한 이유는 값이 없을 때 할당 안되는 경우 null을 세팅하기 위해 사용함(그냥 실무에서 쓰이는 패턴일 뿐 중요한 내용은 X)
    // 자바에서 매개변수는 호출시에 넘겨오는 목적이라면, 스프링에서 매개변수는 HTTP의 URL을 가져오고, 비즈니스 로직에 맞게 DB에 CRUD 작업을 수행하게 됨
    public String path(@PathVariable Long id) {
        System.out.println(id);
        return "OK";
    }

    // case 2) 쿼리 파라미터 방식을 통해 사용자로부터 URL에서 데이터 추출, 주로 검색/정렬 요청 등에서 사용
    // case 2-1) 한 개의 파라미터에서 데이터 추출
    // - 데이터의 형식 : /member/param1?name=홍길동
    @GetMapping("/param1")
    @ResponseBody
    public String param1(@RequestParam(value = "name") String nameValue) {
        System.out.println("Member name === " + nameValue);
        return "OK";
    }

    // case 2-2) 두 개의 파라미터에서 데이터 추출
    // - 데이터의 형식 : /member/param2?name=홍길동&email=hong@naver.com
    @GetMapping("/param2")
    @ResponseBody
    public String param2(@RequestParam(value = "name") String nameValue,
                         @RequestParam(value = "email") String emailValue) {
        System.out.println("Member name === " + nameValue);
        System.out.println("Member email === " + emailValue);
        return "OK";
    }

    // case 2-3) 파라미터의 개수가 많아질 경우 데이터 추출
    // - 데이터의 형식 : /member/param3?name=홍길동&email=hong@naver.com
    @GetMapping("/param3")
    @ResponseBody
//    public String param3(Member member) { // 어노테이션을 생략한 문법
    public String param3(@ModelAttribute Member member) {
        System.out.println("Member === " + member);
        return "OK";
    }

    /*
     * [POST 요청 응답 처리 3가지 형태]
     * 1) url-encoded
     * 2) multipart/form-data
     * 3) JSON
     *
     * - POST 요청은 데이터가 body에 들어온다. (단, 이 때 보안처리가 되어있으려면 HTTPS를 사용해야함)
     * - 이 때 GET 요청으로도 회원가입이 가능은 하다(URL을 통해서 데이터 전송 후 서버에 등록) 하지만 body의 데이터와 달리 URL의 로그가 웹브라우저에 시각적으로 남고 보안의 문제가 발생할 수 있으므로 사용하지 않는 패턴이다. 마찬가지로 POST 역시 URL에 담을수도 있긴 함(맞나?)
     */

    // case 1) body의 content-type이 url-encoded 형식
    // - 데이터의 형식 : body부에 name=홍길동&email=hong@naver.com
    // 이 때 형식이 URL 패턴 중 쿼리파라미터의 구조와 동일하기 때문에 동일한 어노테이션(@RequestParam, @ModelAttribute)으로 데이터를 추출, 바인딩할 수 있다.
    @PostMapping("/url-encoded")
    @ResponseBody
    public String urlEncoded(@ModelAttribute Member member) {
        System.out.println(member);
        return "OK";
    }

    // case 2) body의 content-type이 multipart/form-data 형식
    // - MultipartFile 클래스(타입) : java, spring 에서 파일처리의 편의를 제공해주는 클래스로 보통 클래스 내부에 변수로 선언한다.

    // case 2-1) 한 개의 파일이 있는 경우
    // - 데이터의 형식 : body부에 name=홍길동&email=hong@naver.com&profileImage=(바이너리데이터)
    @PostMapping("/multipart-formdata")
    @ResponseBody
    public String multipartFormData(@ModelAttribute Member member,
                                    @RequestParam(value = "profileImage") MultipartFile profileImg) {
        System.out.println("Member === " + member);
        System.out.println("File name === " + profileImg.getOriginalFilename());
        return "OK";
    }
  // case 2-2) 여러개의 파일이 있는 경우
    @PostMapping("/multipart-formdata-plura")
    @ResponseBody
    public String pluraMultipartFormData(@ModelAttribute Member member,
                                         @RequestParam(value = "profileImages") List<MultipartFile> profileImgList) {
        System.out.println("Member === " + member);
        System.out.println("List size === " + profileImgList.size());
        return "OK";
    }

    // case 3) body의 content-type이 JOSN 형식
    // case 3-1) 일반적인 JSON 데이터 처리
    // - 데이터 형식 : {"name": "홍길동", "email": "hong@naver.com"}
    @PostMapping("/json")
    @ResponseBody
    public String json(@RequestBody Member member) {
        System.out.println("Member === " + member);
        return "OK";
    }
    // case 3-2) 배열 형식의 JSON 데이터 처리
    // - 데이터 형식 : [{"name": "홍길동1", "email": "hong1@naver.com"}, {"name": "홍길동2", "email": "hong2@naver.com"}, {"name": "홍길동3", "email": "hong3@naver.com"}]
    @PostMapping("/json-list")
    @ResponseBody
    public String jsonList(@RequestBody List<Member> memberList) {
        System.out.println("Member === " + memberList);
        return "OK";
    }

    // case 3-3) 중첩된 JSON 데이터 처리
    // - 데이터 형식 : {"name":"홍길동", "email":"hong1@naver.com", "scores":[{"subject":"math", "point":100}, {"subject":"english", "point":90}, {"subject":"korean", "point":100}]}
    @PostMapping("/json-nested")
    @ResponseBody
    public String jsonNested(@RequestBody Student student) {
        System.out.println("Member === " + student);
        return "OK";
    }

    // case 3-4) JSON + file이 함께 있는 데이터 처리
    // - 데이터 형식 : member={"name": "홍", "email": "hong"}&profileImg=바이너리데이터
    // - 결국 multipart-form data 구조 안에 json을 삽입하는 방식으로 진행된다.
    // - json과 file을 함께 처리해야할 때에는 일반적으로 RequestPart 어노테이션 사용
    // - 일반적으로 객체가 복잡하지 않고 파일이 있다면 그냥 멀티파트로만 처리하면 되지만, 객체가 복잡하고 파일이 있다면 멀티파트로 받되, JSON 형식으로 객체를 나눠서 받고 이 때 RequestPart어노테이션을 사용해야한다.
    @PostMapping("/json-file")
    @ResponseBody
    public String jsonWithFile(@RequestPart("member") Member member,
                               @RequestPart("profileImg") MultipartFile profileImg) {
        System.out.println(member);
        System.out.println(profileImg.getOriginalFilename());
        return "OK";
    }

}
