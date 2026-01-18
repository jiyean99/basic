package com.beyond.basic.b2_board.author.repository;

import com.beyond.basic.b2_board.author.domain.Author;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// 실제로는 DB와 통신해야 하지만, 현재는 DB 내역을 List로 대신 저장할 예정
public class AuthorRepository {
    private List<Author> authorList = new ArrayList<>();

    private static Long staticId = 1L;

    // 컨트롤러의 데이터를 서비스로, 서비스의 데이터를 레포지토리로 넘길 때
    // "컨트롤러에서 값이 제대로 담겨서 넘어온 걸까?" 라는 의문이 생길 수 있음
    //
    // 결론: 아니다.
    // 이유: 값을 넘길 때 DTO 구조와 도메인(엔티티) 구조가 다르기 때문에
    //      컨트롤러 단계에서 도메인 객체를 올바르게 만들 수 없음
    // 또한 ID 값은 결국 DB까지 가봐야 알 수 있는 값임
    //
    // 하지만 우리는 편의상 레포지토리에서 ID 값을 static 변수로 직접 생성해서 부여할 것임
    // -> 이게 단순히 편의상인지, 실제 동작과 유사한 방식인지 헷갈릴 수 있음
    // 참고: 일반적으로 ID 값은 DB에서 생성되는 것이 정상이라 보통 set을 해주지 않음
    //       다만 지금은 무언가 값을 넣어줘야 하니까(임시 구현이므로) 직접 부여하는 방식 사용
    public void save(Author author) {
        this.authorList.add(author);
        author.setId(staticId++);
    }

    public List<Author> findAll() {
        List<Author> authorList = new ArrayList<>();
        authorList.addAll(this.authorList);
        return this.authorList;
    }

    // ID가 있을 수도 있고 없을 수도 있으므로 Optional로 감싸서 반환 (주의)
    //
    // 실제 DB라면 select 문을 사용하겠지만,
    // 현재는 List 기반 임시 설계이므로 for문으로 id를 조회할 예정
    public Optional<Author> findById(Long id) {
        Author author = null;
        for (Author a : this.authorList) {
            if (a.getId().equals(id)) {
                author = a;
            }
        }

        return Optional.ofNullable(author);
    }
}
