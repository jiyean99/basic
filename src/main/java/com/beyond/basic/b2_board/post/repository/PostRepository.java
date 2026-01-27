package com.beyond.basic.b2_board.post.repository;

import com.beyond.basic.b2_board.author.domain.Author;
import com.beyond.basic.b2_board.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByDelYn(String delYn);

    Optional<Post> findByIdAndDelYn(Long id, String delYn); // [raw쿼리] select * from where id = ? and delYn = ?

    // 회원 상세 조회 시 쓴 글의 수
//     List<Post> findAllByAuthorIdAndDelYn(Long authorId, String delYn);

    /*
     * TODO [JPQL]
     * - 객체 지향형 raw 쿼리로, 일반 raw쿼리로 작성 시 컴파일타임에서 검사를 해주지 않기때문에 에러발생시 대응이 어려움
     *   JPQL의 경우 컴파일 타임에서 스프링이 내부적으로 검증을 해준다.
     * - JPQL을 사용한 inner join 시, 별도의 on 조건 필요 X
     * (1) JPQL을 활용한 일반 inner join : N+1 문제 해결 X
     * (2) JPQL을 활용한 inner join (fetch) : N+1 문제 해결 O
     */

    // (1) inner join
    // [순수 쿼리 비교] select p.* from Post p inner join Author a on a.id = p.author_id
    // 이미 관계성을 Post 객체에서 갖고있기 때문에 아래의 순수 쿼리와 같은 구조로 쓸 필요가 없어진다.(on 조건 불필요!)
    @Query("select p from Post p "
            + "inner join p.author"
            + " where p.delYn = 'NO'")
    List<Post> findAllInnerJoin();

    // (2) fetch inner join
    // [순수 쿼리 비교] select * from Post p inner join Author a on a.id = p.author_id;
    @Query("select p from Post p "
            + "inner join fetch p.author"
            + " where p.delYn = 'NO'")
    List<Post> findAllFetchInnerJoin();

}
