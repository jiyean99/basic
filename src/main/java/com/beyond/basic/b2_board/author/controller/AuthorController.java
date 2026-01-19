package com.beyond.basic.b2_board.author.controller;

import com.beyond.basic.b2_board.author.dto.AuthorCreateDto;
import com.beyond.basic.b2_board.author.dto.AuthorDetailDto;
import com.beyond.basic.b2_board.author.dto.AuthorListDto;
import com.beyond.basic.b2_board.author.service.AuthorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/author")
public class AuthorController {
    // [AuthorService -> 왜 이 코드를 추가했나]
    // - 컨트롤러 메서드가 실행될 때마다 service를 new로 생성하는 구조를 피하기 위함
    //
    // [기존에 어떤 문제가 있었나]
    // - 메서드 호출 시점마다 AuthorService가 새로 만들어지면 “매번 초기화”가 발생함
    // - 특히 service 내부에 상태값/저장소(List 등)가 있으면, 호출할 때마다 데이터가 날아가는 문제가 생길 수 있음
    //
    // [일단 어떻게 임시로 고쳤나]
    // - 컨트롤러 객체가 생성되는 시점(생성자)에서 AuthorService를 1번만 생성해 필드에 보관함
    // - 이후 컨트롤러의 여러 메서드에서는 같은 service 인스턴스를 재사용함(초기화 반복 방지)
    //
    // [주의(실무 관점)]
    // - 이 방식은 스프링 DI(@Autowired/생성자 주입)를 쓰지 않고 개발자가 직접 new로 의존성을 연결하는 형태임
    // - 실무에서 권장되는 패턴은 아니고, 현재 실습 단계에서 “객체 초기화 문제”를 피하려는 임시 구성임
    private AuthorService authorService;

    public AuthorController() {
        this.authorService = new AuthorService();
    }

    // Author 클래스 변수 name, email, password

    // 1. 회원가입
    // - URL 설계 : /author/create
    // - 데이터 형식 설계 (json, 필요시 multipart/form-data로 변경하게 될 수도 있음) : {"name":"lee", "email":"lee@naver.com", "password":"1234"}
    @PostMapping("/create")
    public String create(@RequestBody AuthorCreateDto dto) {
        authorService.save(dto);
        return "OK";
    }

    // 2. 회원목록조회
    // - URL 설계 : /author/list
    // - 데이터 형식 설계(List 형식의 리턴타입을 가진 JSON 데이터) : [{"name":"lee", "email":"lee@naver.com", "password":"1234"},{"name":"lee", "email":"lee@naver.com", "password":"1234"},{"name":"lee", "email":"lee@naver.com", "password":"1234"}]
    @GetMapping("/list")
    public List<AuthorListDto> findAll() {
        List<AuthorListDto> dtoList = authorService.findAll();
        return dtoList;
    }

    // 3.회원상세조회
    // - URL 설계 : /author/{id} (id만 출력되도록)
    // - 데이터 형식 설계 : 1(text)
    @GetMapping("/{id}")
    public AuthorDetailDto findById(@PathVariable Long id) {
        AuthorDetailDto dto = authorService.findById(id);

        return dto;
    }

    // 4.회원탈퇴 (DeleteMapping 사용)
    // - URL 설계 : /author/{id} (id만 출력되도록)
    // - 데이터 형식 설계 : 1(text)
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        System.out.println("회원 삭제 완료 : " + id);
        return "OK";
    }
}
