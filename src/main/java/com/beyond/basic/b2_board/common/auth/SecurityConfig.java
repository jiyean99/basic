package com.beyond.basic.b2_board.common.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// [싱글톤 객체 생성 비교]
// 내가 만든 클래스와 객체는 @Component 어노테이션을 통해 생성한다.
//  - @Component 클래스 상단에 붙여 해당 클래스 기반의 객체를 싱글톤 객체로 생성한다.
// 외부 클래스(라이브러리)를 활용한 객체는 @Configuration + @Bean 어노테이션 조합으로 생성한다.
//  - @Bean는 메서드 상단에 붙여 return 되는 객체를 싱글톤 객체로 생성한다.
@Configuration
public class SecurityConfig {
//    private final JwtTokenFilter jwtTokenFilter;
//    @Autowired
//    public SecurityConfig(JwtTokenFilter jwtTokenFilter) {
//        this.jwtTokenFilter = jwtTokenFilter;
//    }

    // TODO SecurityFilterChain의 클래스인 HttpSecurity를 통해 객체 생성
    // @@ 빼고는 된다 라는 화이트리스팅
    // 지정한 특정 URL을 제외한 모든 요청에 대해서 인증처리(authenticated) 하겠다 라는 의미
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                // [csrf 옵션] : csrf 공격에 대한 방어 비활성화(일반적으로 세션방식의 쿠키를 활용한 공격)
                .csrf(AbstractHttpConfigurer::disable)
                // [httpBasic 옵션] : 이메일과 비밀번호를 인코딩하여 인증,전송하는 간단한 인증방식을 비활성화
                // - 활용: 간편인증 로그인은 독립적인 로그인이 아니고 기존 로그인에 얹어지는것이고, 간혹 사용처가 있긴 하다
                .httpBasic(AbstractHttpConfigurer::disable)
                // [sessionManagement 옵션] : 세션로그인 방식 비활성화(세션=stateful, 토큰=stateless)
                .sessionManagement(a -> a.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 토큰을 검증하고, Authentication 객체 생성
//                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(a ->
                        a.requestMatchers("/author/create", "/author/login")
                                .permitAll()
                        .anyRequest().authenticated())
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}