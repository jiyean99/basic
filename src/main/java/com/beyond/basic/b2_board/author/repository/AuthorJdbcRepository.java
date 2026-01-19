package com.beyond.basic.b2_board.author.repository;

import com.beyond.basic.b2_board.author.domain.Author;
import com.beyond.basic.b2_board.author.dto.AuthorDetailDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class AuthorJdbcRepository {
    /*
     * TODO [DataSource] - 거의 사용하지 않을 예정
     * - JDBC의 DB 관리 객체
     * - 쿼리 작성을 위해서는 연결 객체가 필요하다
     * - 단점:
     * (1) 쿼리를 직접 작성해야하는 불편함이 있다.
     *   - raw 쿼리에서 오타가 발생하여도 컴파일 에러 발생 X
     *   - 데이터 추가/조회 시 컬럼의 매핑 수작업 필요
     * (2) 데이터 조회 후 객체 조립을 수작업으로 해야한다.
     * */

    private final DataSource dataSource;

    @Autowired
    public AuthorJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void save(Author author) {
        try {
            Connection connection = dataSource.getConnection(); // checked 예외 발생 -> try-catch로 예외를 다시 던짐(롤백의 기준이 되기 위해서)
            String sql = "insert into author(name, email, password) values(?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, author.getName());
            preparedStatement.setString(2, author.getEmail());
            preparedStatement.setString(3, author.getPassword());
            // executeUpdate() : 추가/수정
            // executeQuery() : 조회
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e); // uncheck 예외로 던진다.
        }
    }

    public List<Author> findAll() {
        List<Author> authorList = new ArrayList<>();

        try {
            Connection connection = dataSource.getConnection();
            String sql = "select * from author";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                Author author = Author.builder()
                        .id(id)
                        .name(name)
                        .email(email)
                        .password(password)
                        .build();
                authorList.add(author);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return authorList;
    }

    public Optional<Author> findById(Long inputId) {
        Author author = null;
        try {
            Connection connection = dataSource.getConnection();
            String sql = "select * from author where id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, inputId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                author = Author.builder()
                        .id(id)
                        .name(name)
                        .email(email)
                        .password(password)
                        .build();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.ofNullable(author);
    }
}
