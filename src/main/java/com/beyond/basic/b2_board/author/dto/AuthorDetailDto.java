package com.beyond.basic.b2_board.author.dto;

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
public class AuthorDetailDto {
    private Long id;
    private String name;
    private String email;
    private String password;
    private int postCount;

    // =========================================================
    // [기존 DTO 변환 메서드]
    // - Service에서 계산한 postCount를 파라미터로 받아 세팅
    // - 장점: soft delete 제외 등 “정확한 기준의 개수”를 Service/Repository에서 통제 가능
    // =========================================================
    /*
    public static AuthorDetailDto fromEntity (Author author, int postCount){
        return AuthorDetailDto.builder()
                .id(author.getId())
                .name(author.getName())
                .email(author.getEmail())
                .password(author.getPassword())
                .postCount(postCount)
                .build();
    }
    */

    // =========================================================
    // [변경 DTO 변환 메서드]
    // - postCount를 DTO 내부에서 author.getPostList().size()로 계산
    // - 주의: 연관관계가 LAZY면 size() 시점에 Post 조회 쿼리가 나갈 수 있음
    // - 주의: soft delete(delYn="YES") 글도 postList에 포함되면 개수가 부풀려질 수 있음
    //   (정확히 “미삭제 글 수”가 필요하면 countByAuthorAndDelYn(...) 같은 count 쿼리 권장)
    // =========================================================
    public static AuthorDetailDto fromEntity (Author author){
        return AuthorDetailDto.builder()
                .id(author.getId())
                .name(author.getName())
                .email(author.getEmail())
                .password(author.getPassword())
                .postCount(author.getPostList().size()) // soft delete로 삭제된 게시글도 함께 조회되고있는 문제점은 있음
                .build();
    }
}
