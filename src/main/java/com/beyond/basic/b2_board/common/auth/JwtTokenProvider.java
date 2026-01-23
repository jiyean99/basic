package com.beyond.basic.b2_board.common.auth;

import com.beyond.basic.b2_board.author.domain.Author;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {

    // 중요 정보의 경우 application.yml 에 저장 후 Value 어노테이션을 통해 주입 받아 사용
    // 이 때 반드시 실행순서가 보장되어있어야지만 된다. -> PostConstruct를 통해 순서 보장
    @Value("${jwt.secretKey}")
    // [secret key 규칙]
    // - 인코딩 된 32글자 이상의 긴 문자열 -> 디코딩 -> 암호화
    private String st_secret_key;

    // 인코딩된 문자열 st_secret_key를 디코딩 + 암호화(HS512 알고리즘 사용) 하여 secret_key에 넣어주어야함.
    private Key secret_key;

    // @PostConstruct : 생성자 호출 이후에 아래의 메서드를 실행하게 함으로서, @Value 시점보다 늦게 실행되도록 하여 값 주입의 문제 해결
    @PostConstruct
    public void init(){
        secret_key = new SecretKeySpec(Base64.getDecoder().decode(st_secret_key), SignatureAlgorithm.HS512.getJcaName());
    }

    public String createToken(Author author) {
        // claims 생성
        // [구조] "sub" : "abc@naver.com"
        // 주된 키값을 제외한 나머지 정보는 put을 사용하여 key:value로 세팅
        Claims claims = Jwts.claims().setSubject(author.getEmail());
        claims.put("role", author.getRole().toString());
        // ex) claims.put("age", author.getAge().toString()); 이런식으로 계속 추가하는 형태 가능

        // 절대숫자 <-> 날짜 변환 가능
        Date now = new Date();

        // 토큰 구성 요소 : Header, Payload, Signature(서명부)
        String token = Jwts.builder()
                // 아래 세가지 요소는 payload의 구성이다.
                .setClaims(claims) //sub : 주된 정보
                .setIssuedAt(now) //iat : 발급 시간
                .setExpiration(new Date(now.getTime() + 30 * 60 * 1000L)) //exp : 만료일시(발급시간 기준) -> 30분을 밀리초 형태로 변환(30분 * 60초 * 1000밀리초)
                // secret key를 통해 서명값(signature) 생성
                .signWith(secret_key)
                .compact();
        return token;
    }
}
