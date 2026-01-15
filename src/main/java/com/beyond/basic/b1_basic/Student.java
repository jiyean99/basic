package com.beyond.basic.b1_basic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    private String name;
    private String email;
    private List<Score> scores;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Score{
        private String subject;
        private int point;
    }
}

// Score 클래스를 선언하는 방법은 세가지가 있다.
// 이 때 종속되는 개념이므로 public으로 설계하기보다는 내부클래스로 작성하는것이 좋을듯함
// 또한 내부클래스 중 static 클래스를 선언하는 경우 해당 클래스 상단에도 어노테이션을 작성해줘야한다.
