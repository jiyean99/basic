package com.beyond.basic.b2_board.author.domain;

/**
 * Author 권한 레벨을 정의하는 Enum
 *
 * [Enum 특징]
 * - 내부적으로 숫자값(순서대로 0, 1...)을 가짐
 * - @Enumerated(EnumType.STRING)으로 DB에 문자열로 저장 (Author.java 참조)
 * - 기본 생성자/메서드 불가, 순서대로 ADMIN(0) < USER(1)
 *
 */
public enum Role {
    ADMIN, USER
}
