package com.beyond.basic.b2_board.post.dto;


import com.beyond.basic.b2_board.author.domain.Author;
import com.beyond.basic.b2_board.post.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PostDetailDto {
    private Long id;
    private String title;
    private String category;
    private String contents;
    private String authorEmail;

    /*
    // TODO 설계변경 1
    public static PostDetailDto fromEntity(Post post, Author authorByPostId){
        return PostDetailDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .contents(post.getContents())
                .category(post.getCategory())
                .authorEmail(authorByPostId.getEmail())
                .build();
    }
    */
    // TODO 설계변경 2
    public static PostDetailDto fromEntity(Post post){
        return PostDetailDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .contents(post.getContents())
                .category(post.getCategory())
                .authorEmail(post.getAuthor().getEmail())
                .build();
    }
}
