package com.beyond.basic.b2_board.author.dto;

import com.beyond.basic.b2_board.author.domain.Author;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO 계층은 엔티티만큼의 안정성을 우선하기보다는 편의를 위해 Setter도 일반적으로 추가한다
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorCreateDto {
    // NotEmpty : 값이 비어있으면 안됨을 의미하는 검증하는 어노테이션
    // NotBlank : 공백까지 포함하여 검증하는 어노테이션
    @NotBlank(message = "이름을 작성하시오.")
    private String name;
    @NotBlank(message = "이메일을 작성하시오.")
    private String email;
    @NotBlank(message = "비밀번호를 작성하시오.")
    @Size(min = 8, message = "비밀번호의 길이가 짧습니다. 8자 이상 입력하시오.")
    private String password;

    public Author toEntity() {
        return Author.builder()
                .name(this.name)
                .email(this.email)
                .password(this.password)
                .build();
    }
}
