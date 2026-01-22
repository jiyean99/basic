package com.beyond.basic.b2_board.post.repository;

import com.beyond.basic.b2_board.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByDelYn(String delYn);
    Optional<Post> findByIdAndDelYn(Long id, String delYn); // [raw쿼리] select * from where id = ? and delYn = ?

    // 회원 상세 조회 시 쓴 글의 수
//     List<Post> findAllByAuthorIdAndDelYn(Long authorId, String delYn);

}
