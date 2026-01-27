package com.beyond.basic.b2_board.post.controller;

import com.beyond.basic.b2_board.post.dto.PostCreateDto;
import com.beyond.basic.b2_board.post.dto.PostDetailDto;
import com.beyond.basic.b2_board.post.dto.PostListDto;
import com.beyond.basic.b2_board.post.service.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class PostController {
    // 서비스 계층 DI
    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    // 1.게시글등록(/post/create)
    //-title, contents, category, authorEmail
    //-authorEmail 존재 유효성 체크 => authorRepository 주입 및 findByEmail 호출
    @PostMapping("/post/create")
    @ResponseStatus(HttpStatus.CREATED)
    public void postCreate(@RequestBody @Valid PostCreateDto dto) {
        postService.save(dto);
    }

    // 2.게시글목록조회(/posts)
    //-id, title, category, authorEmail
    //-삭제된 데이터 조회 제외 => List<Post> findByDelYn(String delYn)
    @GetMapping("/posts")
    // 데이터 요청 형식 : localhost:8080/posts?page=0&size=5&sort=title,asc
    // 객체 바인등을 Pageable이 알아서 해줌
    public Page<PostListDto> postListDto(@PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
                                             Pageable pageable) {
        return postService.findAll(pageable);
    }

    // 3.게시글상세조회(/post/1)
    //-id, title, category, contents, authorEmail
    @GetMapping("/post/{id}")
    public PostDetailDto postDetailDto(@PathVariable Long id) {
        return postService.findById(id);
    }

    // 4.게시글삭제(/post/1) => DeleteMapping 쓰면서 실질은 update작업
    @DeleteMapping("/post/{id}")
    public void deletePost(@PathVariable Long id) {
        postService.deletePost(id);
    }
}
