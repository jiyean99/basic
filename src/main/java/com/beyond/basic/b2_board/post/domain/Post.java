package com.beyond.basic.b2_board.post.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Builder
public class Post {
    // id, title(not null), contents(3000자이하), category, authorEmail(not null), delYn(String)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String contents;
    private String category;
    private String authorEmail;
    private String delYn;

    public void deletePost(String delYn){
        this.delYn = "YES";
    }

}
