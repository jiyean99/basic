package com.beyond.basic.b2_board.common.configs;

import com.beyond.basic.b2_board.common.auth.JwtTokenFilter;
import com.beyond.basic.b2_board.common.exception.JwtAuthenticationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.lang.reflect.Array;
import java.util.Arrays;

// [싱글톤 객체 생성 비교]

// 내가 만든 클래스와 객체는 @Component 어노테이션을 통해 생성한다.
//  - @Component 클래스 상단에 붙여 해당 클래스 기반의 객체를 싱글톤 객체로 생성한다.
// 외부 클래스(라이브러리)를 활용한 객체는 @Configuration + @Bean 어노테이션 조합으로 생성한다.
//  - @Bean는 메서드 상단에 붙여 return 되는 객체를 싱글톤 객체로 생성한다.
@Configuration
@EnableMethodSecurity // @PreAuthorize 어노테이션을 사용하기 위한 설정
public class SecurityConfig {
    private final JwtTokenFilter jwtTokenFilter;
    private final JwtAuthenticationHandler jwtAuthenticationHandler;

    @Autowired
    public SecurityConfig(JwtTokenFilter jwtTokenFilter, JwtAuthenticationHandler jwtAuthenticationHandler) {
        this.jwtTokenFilter = jwtTokenFilter;
        this.jwtAuthenticationHandler = jwtAuthenticationHandler;
    }

    // TODO SecurityFilterChain의 클래스인 HttpSecurity를 통해 객체 생성
    // @@ 빼고는 된다 라는 화이트리스팅
    // 지정한 특정 URL을 제외한 모든 요청에 대해서 인증처리(authenticated) 하겠다 라는 의미
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                // [cors 옵션] :
                // 크로스 도메인이 발생하면 CORS 정책으로 인해 막히게 된다.
                .cors(c -> c.configurationSource(corsConfigurationSource()))
                // [csrf 옵션] : csrf 공격에 대한 방어 비활성화(일반적으로 세션방식의 쿠키를 활용한 공격)
                .csrf(AbstractHttpConfigurer::disable)
                // [httpBasic 옵션] : 이메일과 비밀번호를 인코딩하여 인증,전송하는 간단한 인증방식을 비활성화
                // - 활용: 간편인증 로그인은 독립적인 로그인이 아니고 기존 로그인에 얹어지는것이고, 간혹 사용처가 있긴 하다
                .httpBasic(AbstractHttpConfigurer::disable)
                // [sessionManagement 옵션] : 세션로그인 방식 비활성화(세션=stateful, 토큰=stateless)
                .sessionManagement(a -> a.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // [addFilterBefore] : 토큰을 검증하고, Authentication 객체 생성
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                // [exceptionHandling] : 필터계층의 authenticationEntryPoint 예외처리 로직 추가
                // (컨트롤러와 다르게 사용자에게 return할 때 json으로 제공하는 등의 편의는 없다. ObjectMapper로 파싱해야할 수도 있다는 점 감안)
                // - authenticationEntryPoint : 401 인증에러 처리
                // - 403 에러의 경우 Controller 계층에서 예외처리
                .exceptionHandling(e -> e.authenticationEntryPoint(jwtAuthenticationHandler))
                // 아래는 Authentication 객체가 있는지 없는지 검증하는 로직이다.
                .authorizeHttpRequests(a -> a.requestMatchers("/author/create", "/author/login").permitAll().anyRequest().authenticated())
                .build();
    }

    public CorsConfigurationSource corsConfigurationSource (){
        CorsConfiguration configuration = new CorsConfiguration();
        // [setAllowedOrigins] : 허용 가능한 도메인 목록 설정(프론트에 통신 가능한 option 응답)
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000","https://www.jiyean.shop", "http://localhost:5173"));
        // [setAllowedMethods] : 모든 HTTP 메서드(GET, POST, OPTIONS 등) 허용
        configuration.setAllowedMethods(Arrays.asList("*"));
        // [setAllowedHeaders] : 모든 헤더요소 허용(Content-type, Authorization 등)
        configuration.setAllowedHeaders(Arrays.asList("*"));
        // [setAllowCredentials] : 자격증명을 허용(토큰등을 넣을 수 있음)
        configuration.setAllowCredentials(true);

        // 모든 URL 패턴에 대해 위 CORS 정책을 적용하겠다는 의미의 코드
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}