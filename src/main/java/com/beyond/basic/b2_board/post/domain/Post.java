package com.beyond.basic.b2_board.post.domain;

import com.beyond.basic.b2_board.author.domain.Author;
import com.beyond.basic.b2_board.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Builder
public class Post extends BaseTimeEntity {
    // id, title(not null), contents(3000자이하), category, authorEmail(not null), delYn(String)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(length = 3000)
    private String contents;
    private String category;
    @Builder.Default
    private String delYn = "NO";
    /*
     * TODO [초기설계]
     * - Post에 작성자 식별값을 authorEmail(String)로 직접 저장하는 방식
     *
     * [의미/장점]
     * - 구현이 단순함: join 없이 Post만 조회해도 이메일을 바로 확인 가능
     * - 초기에 빠르게 만들기 좋음
     *
     * [한계/문제]
     * - 참조 무결성 보장 불가: 존재하지 않는 email로도 Post insert 가능(DB가 막기 어려움)
     * - Author 이메일이 변경되면, 과거 Post 데이터(authorEmail)도 전부 업데이트해야 정합성 유지됨
     * - 나중에 작성자 이름/권한/프로필 등 Author 정보를 더 쓰고 싶어지면 결국 Author 테이블을 또 조회해야 함
     *   (email 기준 join은 PK/FK 기반 join보다 관리/성능/정합성 측면에서 불리해질 수 있음)
     *
     * [코드]
     * @Column(nullable = false)
     * private String authorEmail;
     */

    /*
     * TODO [설계변경1]
     * - authorEmail 대신 authorId(Long)를 Post에 저장(사실상 FK 값처럼 사용)
     * - Service 레이어에서 authorRepository.findById(authorId)로 Author를 조회해 필요 정보 조립
     *
     * [의미/장점]
     * - PK(id)는 보통 변경되지 않으므로 email보다 정합성/유지보수에 유리
     * - 인덱싱/조인 기준으로도 더 정석적인 키 사용
     *
     * [한계/문제]
     * - DB에 FK 제약조건을 걸지 않으면 여전히 “없는 authorId로 insert”가 가능해서 무결성이 깨질 수 있음
     * - JPA 관점에서는 단순 Long 필드라 연관관계 탐색(post.getAuthor())이 불가능
     * - 매번 Service에서 Author 조회/조립 로직이 반복될 가능성이 큼(코드가 퍼질 수 있음)
     *
     * [코드]
     * @Column(nullable = false)
     * private Long authorId;
     */

    /*
     * TODO [설계변경2] (현재 적용)
     * - JPA 연관관계 매핑으로 Post가 Author 엔티티를 직접 참조하도록 변경
     * - DB에는 author_id(FK) 컬럼이 생기고, JPA가 이 FK로 Author를 매핑/조회해줌
     *
     * [의미/장점]
     * - 객체 그래프 탐색이 자연스러움: post.getAuthor()로 바로 접근 가능
     * - 애플리케이션 객체 관계와 DB 관계(FK)가 일치하기 쉬움
     * - fetch 전략(LAZY/EAGER), join fetch 등 JPA 기능을 제대로 활용 가능
     *
     * [코드 - 현재 적용]
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT), nullable = false)
    private Author author;

    /*
     * =========================================================
     * FK 설정 관련 어노테이션 보충 설명
     * =========================================================
     *
     * [@ManyToOne]
     * - “Post 여러 개가 Author 하나를 참조한다”는 관계(다:1)
     * - 이 매핑을 걸면 Post 테이블에 FK 컬럼이 만들어짐(기본 추론 시 보통 author_id)
     * - fetch FetchType.LAZY vs FetchType.EAGER(디폴트)
     *      - FetchType.LAZY(지연로딩) : 사용하지 않는 불필요한 로딩을 하지 않는 옵션, 서버 부하 감소
     *      - FetchType.EAGER(즉시로딩) : 사용하지 않는 불필요한 로딩을 하지 않는 옵션, 서버 부하 감소
     * - 실무에서는 N+1, 불필요한 즉시로딩 방지를 위해 fetch = LAZY를 명시하는 경우가 많음
     *   예) @ManyToOne(fetch = FetchType.LAZY)
     *
     * [@JoinColumn]
     * - FK 컬럼을 구체적으로 정의하는 어노테이션(@ManyToOne 사용시 기본 옵션으로 작성은 됨)
     * - 컬럼명(name), null 허용(nullable), (필요 시) 참조 컬럼(referencedColumnName) 등을 지정 가능
     *      - foreignKey 설정의 NO_CONSTRAINT: FK 설정 하지 않고자 할 때 설정
     * - @JoinColumn을 생략하면 JPA가 기본 네이밍 규칙으로 FK 컬럼명을 자동 생성함
     *
     * [추천 형태(작성자 필수라면)]
     * @ManyToOne(fetch = FetchType.LAZY, optional = false)
     * @JoinColumn(name = "author_id", nullable = false)
     * private Author author;
     *
     * [주의]
     * - 엔티티에 @ToString 사용 시 연관관계 필드를 포함하면
     *   LAZY 로딩이 의도치 않게 발생하거나(프록시 초기화),
     *   양방향 매핑이면 순환참조(무한루프) 문제가 생길 수 있어 제외(@ToString.Exclude) 고려
     * =========================================================
     */
    public void deletePost() {
        this.delYn = "YES";
    }

}
