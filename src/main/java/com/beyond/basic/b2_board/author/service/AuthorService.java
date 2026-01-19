package com.beyond.basic.b2_board.author.service;

import com.beyond.basic.b2_board.author.domain.Author;
import com.beyond.basic.b2_board.author.dto.AuthorCreateDto;
import com.beyond.basic.b2_board.author.dto.AuthorDetailDto;
import com.beyond.basic.b2_board.author.dto.AuthorListDto;
import com.beyond.basic.b2_board.author.repository.AuthorRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class AuthorService {
    // [왜 문제가 있었나]
    // - 서비스 메서드 실행 시점마다 AuthorRepository를 new로 생성하는 구조였으면, 레포지토리 내부의 in-memory 데이터(List)와 staticId 같은 상태가 매번 "초기 상태"로 돌아갈 수 있음.
    // - 그 결과, 저장했던 데이터가 다음 요청/메서드 호출에서 조회되지 않거나(리스트가 비어있음), 동작이 "호출할 때마다 리셋되는 것처럼" 보여서 실습 흐름이 깨지는 문제가 생김.
    //
    // [일단 어떻게 임시로 고쳤나]
    // - AuthorService가 생성되는 시점(생성자)에서 AuthorRepository를 1번만 생성해서 필드로 들고 있게 함.
    // - 이후 서비스의 여러 메서드에서는 this.authorRepository를 재사용하므로 매번 new로 인한 초기화(리셋) 문제를 임시로 방지할 수 있음.
    //
    // [참고]
    // - 레파지토리에서 매번 새로운 객체를 생성하여 쿼리를 통해 DB 데이터를 주입하는거였다면 큰 문제는 없었을 것이다.
    // - 하지만 스프링을 제대로 쓰는 구조라면 @Repository/@Service로 빈 등록 후 생성자 주입(DI)으로 의존성을 주입받아 싱글톤으로 관리하는 방식이 일반적임.
    private AuthorRepository authorRepository;

    public AuthorService() {
        authorRepository = new AuthorRepository();
    }

    public void save(AuthorCreateDto dto) {
        // 객체 직접 조립
        Author author = new Author(null, dto.getName(), dto.getEmail(), dto.getPassword());
        authorRepository.save(author);
    }

    public List<AuthorListDto> findAll() {
        List<Author> authorList = authorRepository.findAll();
        List<AuthorListDto> authorListDtoList = new ArrayList<>();
        for (Author a : authorList) {
            AuthorListDto dto = new AuthorListDto(a.getId(), a.getName(), a.getEmail());
            authorListDtoList.add(dto);
        }
        return authorListDtoList;
    }

    public AuthorDetailDto findById(Long id) {
        Optional<Author> optionalAuthor = authorRepository.findById(id);
        Author author = optionalAuthor.orElseThrow(() -> new NoSuchElementException("Entity is not found")); // 추후 JPA 의존성 설치 후에는 Entity가 없다는 에러를 던질것이다.

        // DTO 조립
        AuthorDetailDto dto = new AuthorDetailDto(author.getId(), author.getName(), author.getEmail(), author.getPassword());

        return dto;
    }

}
