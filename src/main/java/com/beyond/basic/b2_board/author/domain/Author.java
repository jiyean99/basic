package com.beyond.basic.b2_board.author.domain;

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
 */
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Author {
    private Long id;
    private String name;
    private String email;
    private String password;
}
