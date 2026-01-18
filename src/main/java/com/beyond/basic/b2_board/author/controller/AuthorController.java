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
