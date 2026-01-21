package com.beyond.basic.b2_board.author.domain;

import jakarta.persistence.*;
import lombok.*;

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
 */
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(length = 50, unique = true, nullable = false)
    private String email;
    //@Column(name = "pw")
    private String password;

    public void updatePassword(String password){
        this.password = password;
    }
}
