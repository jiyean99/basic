package com.beyond.basic.b2_board.author.repository;

import com.beyond.basic.b2_board.author.domain.Author;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// 실제로는 DB와 통신해야 하지만, 현재는 DB 내역을 List로 대신 저장할 예정
public class AuthorRepository {
    // [컨트롤러 → 서비스 → 레포지토리로 값이 넘어갈 때 생기는 의문]
    // - 컨트롤러에서 받은 값이 서비스/레포지토리까지 “제대로 담겨서” 넘어오는 걸까? 라는 의문이 생길 수 있음
    //
    // [결론]
    // - 아니다.
    //
    // [이유]
    // - 값을 넘기는 과정에서 DTO 구조와 도메인(엔티티) 구조가 다를 수 있음
    // - 그래서 컨트롤러 단계에서는 도메인 객체를 “완벽하게” 만들기 어려운 경우가 생김
    //
    // [특히 ID 문제]
    // - ID 값은 원래 DB에 insert가 수행된 이후에야(= DB까지 가봐야) 확정되는 값임
    //
    // [현재 실습에서의 임시 처리]
    // - 실제 DB가 없으므로, 편의상 레포지토리에서 static 변수를 사용해 ID를 직접 생성/부여함
    // - 이 방식이 단순 편의인지, 실제 동작과 유사한지 헷갈릴 수 있는데,
    //   실무 기준으로는 “DB가 생성해주는 ID를 대체하기 위한 임시 구현”이라고 보면 됨
    //
    // [참고]
    // - 일반적으로 ID는 DB에서 생성되는 것이 정상이라, 애플리케이션 코드에서 보통 set을 직접 하지 않음
    // - 다만 지금은 메모리(List)로 DB를 흉내내는 상황이라 “식별자”가 필요해서 직접 부여하는 방식 사용
    private List<Author> authorList = new ArrayList<>();

    private static Long staticId = 1L;

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
