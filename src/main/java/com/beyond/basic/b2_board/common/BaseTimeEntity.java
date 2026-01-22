package com.beyond.basic.b2_board.common;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/*
* [@MappedSuperclass]
* - 기본적으로는 Entity는 상속이 불가능한 구조다.
* - 예외적으로 @MappedSuperClass라는 어노테이션을 통해 상속관계 설정을 해준다.
* - 이 때 선후관계의 문제로 인해서 상속받은 위치에서 Getter어노테이션이 먼저 실행되고 상속을 받게 되면 해당 엔티티의 Getter 기능을 수행하지 못하기 때문에 별도로 작성해준다.
*/
@MappedSuperclass
@Getter
public class BaseTimeEntity {
    @CreationTimestamp
    private LocalDateTime createdTime;
    @UpdateTimestamp
    private LocalDateTime updatedTime;
}
