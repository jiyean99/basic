package com.beyond.basic.b2_board.author.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
// Author 요구사항: name, email, password
//
// 서비스의 목적에 따라 return(응답)해야 하는 값들이 달라지므로,
// Author 하나로 모든 서비스의 요청/응답을 커버할 수 없음
//
// 결국 Author는 DB에 들어가게 되는 원형 엔티티일 뿐이고,
// 사용자의 요청(request)과 응답(response) 케이스를 구별해야 함 (DTO)
//
// domain은 DB와 동일한 데이터 구조를 가져야 함
public class Author {
    private Long id;
    private String name;
    private String email;
    private String password;
}
