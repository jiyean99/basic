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
