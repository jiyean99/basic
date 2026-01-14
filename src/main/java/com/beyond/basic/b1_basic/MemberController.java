package com.beyond.basic.b1_basic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

// Controller 어노테이션을 통해 스프링에 의해 객체가 생성되고, 관리되어 개발자가 직접 객체를 생성할 필요 없음.
// @Controller 어노테이션을 선언하는 순간, @Controller 어노테이션과 url 매핑을 통해 사용자의 요청이 메서드로 분기처리된다.
// Controller를 통해 내부적으로 new 객체를 만들어주고, 사용자 요청을 통해 라우팅 시켜주게 되는 것
// 이 때 controller 레벨에서는 메서드 이름이 크게 유의미 하지 않다. (url 패턴이 유의미하고 그냥 가독성만을 위해 작성한다고 생각해라)
@Controller
@RequestMapping("/member") // 해당 어노테이션으로 공통처리
public class MemberController {
    // GET 요청 리턴의 case : 1)text, 2)JSON, 3)html(MVC 아키텍쳐)

    // 우리 눈에는 안보이지만 http 문서를 만들어서 사용자에게 보냈고(우리는 바디부분만 세팅), 원하는 상태 코드와 상태 문구등 커스텀 조작이 가능하다.

    // case1) 서버가 사용자에게 text 데이터 return
    // ResponseBody : 데이터(text, json)를 리턴하는 경우 사용, 화면(html)을 리턴하는 경우는 ResponseBody 생략(생략된 경우는 templates로 화면을 찾으러 감)
    // RestController : Controller + ResponseBody, 해당 어노테이션 사용 시 화면을 보여줄 수 없음(MVC 패턴 불가)
    @GetMapping("")
    @ResponseBody
    public String textDataReturn() {

        return "홍길동";
    }

    // case2) 서버가 사용자에게 json 형식의 문자 데이터 return
    @GetMapping("/json")
    @ResponseBody
    public Member jsonDataReturn() throws JsonProcessingException {
        Member m1 = new Member("이지연", "jiyean@naver.com");
        // 직접 JSON을 직렬화할 필요 없이 return 타입에 객체가 있다면 자동으로 직렬화해준다.
        // 또한 아래와 같이 return 하게 되면 JSON이 아니고 String으로 return이 된다.
        //ObjectMapper o1 = new ObjectMapper();
        //String data = o1.writeValueAsString(m1);
        return m1;
    }

    // case3) 서버가 사용자에게 html return
    // ResponseBody가 없고, return 타입은 String인 경우 스프링은 templates 폴더 하위의 return된 문자열과 동일한 html 문서를 조회하여 return
    // 단, 타임리프, JSP 등의 의존성 필요
    @GetMapping("/html")
    public String htmlReturn() {
        return "simple_html";
    }
}
