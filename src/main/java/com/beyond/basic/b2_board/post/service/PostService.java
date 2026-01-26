package com.beyond.basic.b2_board.post.service;

import com.beyond.basic.b2_board.author.domain.Author;
import com.beyond.basic.b2_board.author.repository.AuthorRepository;
import com.beyond.basic.b2_board.post.domain.Post;
import com.beyond.basic.b2_board.post.dto.PostCreateDto;
import com.beyond.basic.b2_board.post.dto.PostDetailDto;
import com.beyond.basic.b2_board.post.dto.PostListDto;
import com.beyond.basic.b2_board.post.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PostService {
    // 레파지토리 계층 DI
    private final PostRepository postRepository;
    private final AuthorRepository authorRepository;

    @Autowired
    public PostService(PostRepository postRepository, AuthorRepository authorRepository) {
        this.postRepository = postRepository;
        this.authorRepository = authorRepository;
    }

    public void save(PostCreateDto dto) {
        //[기존] : email을 DTO 구조로 설계
        //Author authorByEmail = authorRepository.findAllByEmail(dto.getAuthorEmail()).orElseThrow(() -> new EntityNotFoundException("가입된 회원이 아닙니다."));

        //[개선] : 이미 filter를 타고온 이후이기 때문에 Authentication 객체 안에서 꺼내오는 구조로 개선
        // - getPrincipal : email로 세팅한 principal을 꺼내는 작업(Object 설계였어서 toString화 필요)
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        System.out.println("email === " + email);
        Author authorByEmail = authorRepository.findAllByEmail(email).orElseThrow(() -> new EntityNotFoundException("가입된 회원이 아닙니다."));

        postRepository.save(dto.toEntity(authorByEmail));
    }

    @Transactional(readOnly = true)
    public List<PostListDto> findAll() {
        List<Post> postList = postRepository.findAllByDelYn("NO");
        List<PostListDto> postListDtoList = new ArrayList<>();

        for (Post p : postList) {
            // [변경1]
            //Author authorByPostId = authorRepository.findById(p.getAuthorId()).orElseThrow(() -> new EntityNotFoundException("가입된 회원이 아닙니다."));
            //PostListDto dto = PostListDto.fromEntity(p, authorByPostId);
            // [변경2]
            PostListDto dto = PostListDto.fromEntity(p);
            postListDtoList.add(dto);
        }

        return postListDtoList;
    }


    @Transactional(readOnly = true)
    public PostDetailDto findById(Long id) {
//        Post post = postRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("해당 ID값의 게시글이 존재하지 않습니다."));
//
//        if(post.getDelYn().equals("YES")){
//            throw new EntityNotFoundException("삭제된 게시글입니다.");
//        }

        Post post = postRepository.findByIdAndDelYn(id, "NO")
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 게시글이 없거나 삭제되었습니다."));
        // TODO 설계변경 1
        // Author authorByPostId = authorRepository.findById(post.getAuthorId()).orElseThrow(() -> new EntityNotFoundException("entity is not found"));
        // return PostDetailDto.fromEntity(post, authorByPostId);
        // TODO 설계변경 2
        return PostDetailDto.fromEntity(post);
    }

    public void deletePost(Long id) {
        /*
        // TODO [hard delete 예시]
        Post post = postRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 게시글입니다."));
        postRepository.delete(post);
        */

        // TODO [soft delete 예시 - del_yn]
        Post post = postRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("해당 ID값을 가진 게시글이 존재하지 않습니다."));
        post.deletePost();
    }

}
