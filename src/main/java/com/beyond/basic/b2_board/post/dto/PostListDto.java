package com.beyond.basic.b2_board.post.dto;

import com.beyond.basic.b2_board.author.domain.Author;
import com.beyond.basic.b2_board.post.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostListDto {
    //-id, title, category, authorEmail
    private Long id;
    private String title;
    private String category;
    private String authorEmail;

    /*
    public static PostListDto fromEntity(Post post, Author authorByPostId){
        return PostListDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .category(post.getCategory())
                .authorEmail(authorByPostId.getEmail())
                .build();
    }*/
    public static PostListDto fromEntity(Post post) {
        return PostListDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .category(post.getCategory())
                .authorEmail(post.getAuthor().getEmail())
                .build();
    }

}
