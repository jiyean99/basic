package com.beyond.basic.b2_board.author.domain;

import com.beyond.basic.b2_board.common.BaseTimeEntity;
import com.beyond.basic.b2_board.post.domain.Post;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/*
 * [Lombok 사용 원칙]
 * - 객체의 안정성을 위해 Setter 사용을 지양해야 하므로,
 *   @Data(= Getter/Setter/ToString 등 포함) 대신 @Getter + @ToString을 각각 사용한다.
 *
 * [Builder 패턴 사용]
 * - 객체 생성을 유연하게 하기 위해 Builder 패턴을 사용한다.
 *
 * [Domain(Entity)와 DTO 분리 이유]
 * - 서비스의 목적에 따라 return(응답)해야 하는 값들이 달라지므로,
 *   Author 하나로 모든 서비스의 요청/응답을 커버할 수 없다.
 * - 결국 Author는 DB에 들어가게 되는 원형 엔티티일 뿐이고,
 *   사용자의 요청(request)과 응답(response) 케이스는 DTO로 구별해야 한다.
 *
 * [Domain 설계 원칙]
 * - domain은 DB와 동일한 데이터 구조를 가져야 한다.
 *
 * =========================================================
 * JPA 관련 설정
 * =========================================================
 * [@Entity]
 * - JPA에게 entity 관리를 위임하기 위한 어노테이션 (create 모드일 시 테이블 생성, 컬럼 추가 등)
 * - 변수명이 컬럼명으로 그대로 생성된다. 이 때 camel case를 사용하게 되면 언더스코어로 변경해준다. ex) nickName -> nick_name
 * - Long : big int
 * - Integer : int
 * - String : varchar
 * - Boolean : boolean
 *
 * [@Id]
 * - PK 설정
 *
 * [@GeneratedValue]
 * - GenerationType.IDENTITY : autho_increment 설정
 * - GenerationType.AUTO : id 생성 전략을 JPA에게 자동설정하도록 위임
 *
 * [@Column]
 * - length : 길이 설정(별도의 길이 설정이 없으면 255가 default)
 * - unique : 중복 여부 설정
 * - nullable : null 허용여부 설정
 * - name : 스키마 내 컬럼명 변경(컬럼명 변경은 가능하나 일반적으로 일치시킨다.)
 *
 * [@Enumerated]
 * - enum은 내부적으로 숫자값을 갖고있는 자료형으로 해당 어노테이션을 통해 자료형을 변경해준다. (EnumType.STRING)
 *
 * [@CreationTimestamp]
 * -
 *
 * [@UpdateTimestamp]
 * -
 */
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Author extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(length = 50, unique = true, nullable = false)
    private String email;
    //@Column(name = "pw") 이와 같이 필드명을 개별적으로 수정하는 어노테이션 설정도 가능
    private String password;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.USER;
    // 아래 사항은 공통 엔티티에서 관리 후 상속받는 관계로 수정
//    @CreationTimestamp
//    private LocalDateTime createdTime;
//    @UpdateTimestamp
//    private LocalDateTime updatedTime;

    /*
     * TODO [@OneToMany]
     * - 1(Author) : N(Post) 관계를 나타냄.
     *   (Author 한 명이 여러 Post를 가질 수 있는 구조)
     *
     * [mappedBy 옵션]
     * - 양방향 연관관계에서 “연관관계의 주인(owner)”이 누구인지 지정하는 옵션.
     * - 일반적으로 OneToMany는 선택사항, ManyToOne은 필수사항이다.
     * - mappedBy 값에는 반대편 엔티티(Post) 에서 Author를 참조하는 필드명을 "문자열"로 넣는다.
     *   예) Post 엔티티에 `private Author author;` 라면 -> mappedBy = "author"
     * - 의미:
     *   - FK(외래키)는 DB에서 Post 테이블에 존재하고(Post.author_id), 이 FK를 실제로 관리(INSERT/UPDATE)하는 주인은 Post(@ManyToOne) 쪽이다.
     *   - Author(@OneToMany) 쪽은 “나는 Post.author에 의해 매핑된 컬렉션이야” 라고 알려주는 읽기 전용(반대편) 역할이다.
     * - 주의:
     *   - mappedBy는 “조회 컬럼을 지정”하는 옵션이 아니라, “어떤 필드가 이 관계를 매핑하고 있는지(주인이 누구인지)”를 지정하는 옵션이다.
     *
     * [fetch 옵션]
     * - @OneToMany의 기본 fetch 전략은 LAZY다.
     * - 혼동을 줄이기 위해 명시적으로 작성해두는 경우가 많다.
     *   예) @OneToMany(fetch = FetchType.LAZY, mappedBy = "author")
     *
     * [cascade 옵션]
     * - 부모쪽에 설정해야하는 옵션
     * - CascadeType.REMOVE : a1이 작성한 post 모두 삭제
     * - CascadeType.PERSIST : a1을 저장할 때 p1, p2, p3도 함께 저장 ex) 주문데이터 + 주문상세1 + 주문상세2 + 주문상세3 등의 상황
     *
     * [orphanRemoval 옵션]
     * - 자식의 자식까지 연쇄적으로 삭제해야하는 경우 모든 부모에 orphanRemoval = true 옵션을 작성해줘야만한다.
     */
    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default // PERSIST 옵션 사용시 초기값 세팅이 필요
    private List<Post> postList = new ArrayList<>();

    @OneToOne(mappedBy = "author", fetch = FetchType.LAZY)
    private Address address;

    public void updatePassword(String password) {
        this.password = password;
    }
}
