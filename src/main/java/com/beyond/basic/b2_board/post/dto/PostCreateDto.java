package com.beyond.basic.b2_board.post.dto;

import com.beyond.basic.b2_board.author.domain.Author;
import com.beyond.basic.b2_board.post.domain.Post;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PostCreateDto {
    @NotBlank(message = "제목을 작성하시오.")
    private String title;
    @NotBlank(message = "본문을 작성하시오.")
    @Size(max = 3000, message = "3000자 이하로 작성하시오.")
    private String contents;
    @NotBlank(message = "카테고리를 작성하시오.")
    private String category;
//    @NotBlank(message = "작성자 이메일을 작성하시오.")
//    private String authorEmail;
    private String delYn;

    public Post toEntity(Author authorByEmail) {
        // TODO [설계변경 1 : authorEmail -> authorId]
        /*
        return Post.builder()
                .title(this.title)
                .contents(this.contents)
                .category(this.category)
                .authorId(authorByEmail.getId())
                .build();
        */
        // TODO [설계변경 1 : authorId -> authorByEmail 객체]
        return Post.builder()
                .title(this.title)
                .contents(this.contents)
                .category(this.category)
                .author(authorByEmail)
                .build();
    }
}
