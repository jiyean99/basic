package com.beyond.basic.b2_board.author.repository;

import com.beyond.basic.b2_board.author.domain.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/*
 * [Spring Data JPA - Repository]
 * - Spring Data JPA에서 Repository는 보통 JpaRepository<T, ID>를 상속받아 정의한다.
 *   - T: 엔티티 타입(예: Author), ID: 식별자(PK) 타입(예: Long)처럼 제네릭으로 지정한다.
 *
 * - 구현 클래스를 직접 작성하지 않아도, 스프링 데이터가 런타임에 리포지토리 인터페이스의 구현체(프록시)를 생성해 빈으로 등록한다.
 *   - 내부적으로는 보통 SimpleJpaRepository를 타깃으로 하는 프록시가 만들어져 메서드 호출을 위임한다는 설명이 널리 알려져 있다.
 *
 * - JpaRepository를 상속하면 기본 CRUD 및 유틸 메서드를 기본 제공받는다.
 *   - 예: save(), findById()(Optional 반환), findAll(), deleteById()/delete() 등.
 * - 그 외 다른 컬럼으로 조회를 할 때에는 findBy+컬럼명() 형식으로 선언하면 실행시점 자동 구현됨.
 *
 * - 참고: Spring Data JPA 리포지토리는 @Repository를 명시하지 않아도(설정된 리포지토리 스캔 하에서) 빈으로 등록되는 경우가 일반적이다.
 * */
@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    Optional<Author> findByEmail(String email);
}
