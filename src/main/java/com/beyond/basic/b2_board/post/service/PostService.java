package com.beyond.basic.b2_board.post.service;

import com.beyond.basic.b2_board.author.repository.AuthorRepository;
import com.beyond.basic.b2_board.post.domain.Post;
import com.beyond.basic.b2_board.post.dto.PostCreateDto;
import com.beyond.basic.b2_board.post.dto.PostDetailDto;
import com.beyond.basic.b2_board.post.dto.PostListDto;
import com.beyond.basic.b2_board.post.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Post post = dto.toEntity();
        authorRepository.findByEmail(dto.getAuthorEmail()).orElseThrow(() -> new EntityNotFoundException("가입된 회원이 아닙니다."));
        postRepository.save(post);
    }

    @Transactional(readOnly = true)
    public List<PostListDto> findAll() {
        return postRepository.findByDelYn("NO")
                .stream()
                .map(a -> PostListDto.fromEntity(a))
                .collect(Collectors.toList());
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
        return PostDetailDto.fromEntity(post);
    }

    public void deletePost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("해당 ID값을 가진 게시글이 존재하지 않습니다."));
        post.deletePost(post.getDelYn());
    }

}
