package com.beyond.basic.b1_basic;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

/*
 * [@Data]
 * - @Getter + @Setter + @ToString 통합 어노테이션
 * - DTO와 Entity 사용 주의
 *
 * [생성자 관련 Lombok 어노테이션 : @AllArgsConstructor, @NoArgsConstructor]
 * [@AllArgsConstructor]
 * - 모든 필드를 파라미터로 받는 생성자를 자동 생성함
 * - 예: new Member(name, email) 같은 형태로 쉽게 객체 생성 가능
 * [@NoArgsConstructor]
 * - 파라미터가 없는 기본 생성자를 자동 생성함
 * - 프레임워크(Spring/JPA/Jackson 등)가 리플렉션으로 객체를 만들 때 필요해지는 경우가 많음
 * - (참고) @RequiredArgsConstructor: final 필드 또는 @NonNull 필드만 받는 생성자를 자동 생성함
 * - 불변 객체를 만들거나, 꼭 필요한 값만 강제하고 싶을 때 자주 사용함
 *
 * [MultipartFile]
 * - java, spring 에서 파일처리의 편의를 제공해주는 클래스
 */

/*
 * [DTO와 엔티티 관련 개념 추가 설명]
 *
 * 1) 파싱에 필요한 메서드/생성자
 * - JSON 파싱: getter, 기본 생성자
 * - HTTP 파라미터 파싱: setter, getter, 기본 생성자
 *
 * 2) Setter에 대한 원칙과 현실적인 타협
 * - 원칙적으로 JSON 파싱 시에는 setter를 설정하지 않아야 하는 것이었음
 * - 하지만 HTTP 파라미터 파싱 시에는 setter가 필요함
 * - 따라서 클래스 종류에 따라 언제는 @Data(Setter+Getter)를 깔고, 언제는 Setter를 제외시킬지를 외워야 함
 * - DTO 객체를 보면 이해가 될 것임
 *
 * 3) DTO vs Entity를 나누는 이유
 * - 사용자가 보내오는 값과 DB에 저장되는 값이 항상 동일할까? 일반적으로는 다름
 *   - 예: id, 회원가입 시간 등은 DB에서는 담겨야 하는 값임
 * - 즉, HTTP 요청에서 객체로 받아내고 이를 바로 DB에 저장시키지 않음
 * - 실제 DB에 들어갈 객체를 생성하고 이 객체를 DB에 저장시키는 구조임
 *
 * 4) 용어 정의
 * - HTTP 요청에서 받아내서 만든 객체: Data Transfer Object(DTO)
 * - 실제 DB에 들어가는 객체: Entity
 *
 * 5) 객체 안정성과 Setter 사용 기준
 * - 객체의 안정성이 높아야 하는 객체는 엔티티가 됨
 * - 엔티티는 정제되고 안정적이여하므로 Setter를 쓰지 않음
 * - DTO는 편하게 Setter를 씀
 *
 * 6) 결론(실무 적용 규칙)
 * - DTO에서는 @Data 어노테이션을 사용함
 * - Entity에서는 @Data 어노테이션 사용을 지양하고, @Getter와 생성자 어노테이션을 별도로 사용해줘야 함
 */


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Member {
    private String name;
    private String email;
//    private MultipartFile profileImage;
}
