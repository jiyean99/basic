package com.beyond.basic.b2_board.author.repository;

import com.beyond.basic.b2_board.author.domain.Author;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class AuthorJpaRepository {
    /*
     * =========================================================
     * [EntityManager]
     * =========================================================
     * JPA의 핵심 인터페이스로 해당 인터페이스의 메서드를 사용하여 DML문 생성(객체 주입)
     *
     * [순수 JPA의 특징]
     *  - 순수 JPA의 경우 제한된 메서드 제공으로, JPQL을 사용하여 직접 쿼리를 작성하는 경우가 있음.
     *  - JPQL: JPA에서 쓰는 쿼리로 순수 쿼리(raw 쿼리)와는 다른 객체 지향 쿼리문이다.
     *          엔티티명/필드명을 기준으로 사용, 별칭 사용 가능
     *          순수쿼리의 경우 컴파일에러가 발생하지 않기 때문에 해당 문법으로 작성해야한다.(spring data jpa에서)
     *
     * [순수 JPA의 주요메서드]
     *  - persist() : 데이터를 INSERT 해주는 메서드
     *  - find() : PK로 데이터를 SELECT 하는 메서드
     *  - createQuery() :
     *  - remove() : 순수 jpa에서 제공되는 삭제 메서드
     * */

    // EntityManager 주입
    private final EntityManager entityManager;

    @Autowired
    public AuthorJpaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Optional<Author> findByEmail(String email) {
        List<Author> authorList = entityManager
                .createQuery("select a from Author a where a.email = :email", Author.class)
                .setParameter("email", email)
                .getResultList();

        Author author = null;
        if (!authorList.isEmpty()) {
            author = authorList.get(0);
        }

        return Optional.ofNullable(author);
    }

//    public Optional<Author> findByEmail(String email) {
//        List<Author> result = entityManager
//                .createQuery("select a from Author a where a.email = :email", Author.class)
//                .setParameter("email", email)
//                .getResultList();
//
//        return result.stream().findFirst();
//    }

    public void save(Author author) {
        // persist() : 순수 JPA에서 데이터를 INSERT 해주는 메서드
        // 기존에 author에서 getName, getEmail등으로 값을 꺼낸 후 insert 하던 구조에서 persist를 통해 수행함
        entityManager.persist(author);
    }

    public Optional<Author> findById(Long id) {
        // find() : 순수 JPA에서 PK로 데이터를 SELECT 하는 메서드
        Author author = entityManager.find(Author.class, id);
        return Optional.ofNullable(author);
    }

    public List<Author> findAll() {
        List<Author> authorList = entityManager
                // 엔티티와 변수명을 기준으로 조회해아하므로 Author로 작성
                // 별칭 사용 가능으로 a 작성
                .createQuery("select a from Author a", Author.class)
                .getResultList();
        return authorList;
    }

    public void delete(Long id) {
        // remove() : 순수 jpa에서 제공되는 삭제 메서드, 객체지향적인 모습 확인 가능 -> 추후에는 매개변수로 id를 받기보다는 객체로 받는것이 더 적합할듯
        Author author = entityManager.find(Author.class, id);
        entityManager.remove(author);
    }
}
