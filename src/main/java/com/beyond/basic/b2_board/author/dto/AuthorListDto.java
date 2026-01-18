package com.beyond.basic.b2_board.author.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthorListDto {
    private Long id;
    private String name;
    private String email;
}

