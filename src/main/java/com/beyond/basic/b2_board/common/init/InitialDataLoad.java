package com.beyond.basic.b2_board.common.init;

import com.beyond.basic.b2_board.author.domain.Author;
import com.beyond.basic.b2_board.author.domain.Role;
import com.beyond.basic.b2_board.author.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

// [CommandLineRunner] :
// - CommandLineRunner를 구현함으로써 아래 run 메서드가 스프링빈으로 등록되는 시점에 자동실행
// - 서버가 시작되는 시점에 Components를 스캔하게 될것이고, 호출시점에 싱글톤 객체를 생성하면서 이 때 어드민 사용자를 만들고자하는 목적이다.
// - 즉, “서버 기동 직후 1회 실행되는 초기화 로직”을 넣기에 적합한 위치

// [Component] : 서비스의 로직을 수행하지만 해당 계층으로 묶기는 애매한 부분이 있으니 Component 어노테이션으로만 싱글톤 객체를 생성하였다.
// - 스프링이 컴포넌트 스캔으로 빈 등록 → 실행 시점에 run()이 호출되도록 하기 위해 필요

// [Transactional] : 쿼리를 수행하는 계층이므로 Transaction 처리가 필요하다.
// - 존재 여부 조회 + 저장(insert) 로직을 “한 트랜잭션”으로 묶어, 중간 실패 시 롤백되게 함(원자성/안정성)
@Component
@Transactional
public class InitialDataLoad implements CommandLineRunner {
    private final AuthorRepository authorRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public InitialDataLoad(AuthorRepository authorRepository, PasswordEncoder passwordEncoder) {
        this.authorRepository = authorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // admin 계정이 이미 존재하면 중복 생성 방지
        if (authorRepository.findAllByEmail("admin@naver.com").isPresent()) {
            return;
        }

        // 최초 1회(없을 때만) admin 계정 생성
        // - 비밀번호는 PasswordEncoder로 해시 처리 후 저장(평문 저장 방지)
        authorRepository.save(Author.builder()
                .name("admin")
                .email("admin@naver.com")
                .role(Role.ADMIN)
                .password(passwordEncoder.encode("12341234"))
                .build());
    }
}
