package com.beyond.basic.b2_board.common.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.*; // 서블릿 필터 관련 인터페이스/예외
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component; // 스프링 빈 등록용 어노테이션

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * [역할] : 요청(Request)마다 JWT 토큰을 확인하고,
 * 토큰이 유효하면 "인증된 사용자"로 인식될 수 있도록 Authentication(인증 객체)을 만든 뒤
 * SecurityContext 등에 저장하는 역할을 담당하는 필터 클래스.
 * <p>
 * - 보통 Spring Security 필터 체인 중간에 끼워 넣어 "매 요청마다" 실행됩니다.
 * - 토큰이 없거나 유효하지 않아도, 정책에 따라 그냥 다음 필터로 넘길 수도 있고(비인증 처리),
 * 즉시 401을 반환할 수도 있습니다.
 */
@Component // 스프링 빈 등록용 어노테이션
public class JwtTokenFilter extends GenericFilter {

    @Value("${jwt.secretKey}")
    private String st_secret_key;

    /**
     * 필터의 핵심 메서드.
     * 클라이언트 요청이 들어오면 이 메서드가 실행되고,
     * 여기서 JWT를 꺼내서 검증한 다음 다음 필터로 요청을 전달합니다.
     */
    // 토큰은 servletRequest 객체에 담겨있고, 이는 스프링이 주입해준 것이며 servletResponse가 최종적으로 사용자에게 HTTP 문서로 return 되는 것
    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        // 필터체인으로 다시 빠져나가도록 try-catch를 수행하고, 그 다음줄에서 에러가 터질것이다.(Authentication 객체가 있는지 없는지 검증로직에서)
        try {
            // 1) (보통) ServletRequest/Response는 HttpServletRequest/Response로 캐스팅해서 사용합니다.
            //    이유: 헤더(Authorization)에서 토큰을 꺼내려면 HTTP 전용 API가 필요하기 때문입니다.
            //    예)
            //    HttpServletRequest request = (HttpServletRequest) servletRequest;
            //    HttpServletResponse response = (HttpServletResponse) servletResponse;

            HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

            // 2) (보통) Authorization 헤더에서 "Bearer {token}" 형태로 토큰을 꺼냅니다.
            //    예)
            //    String authHeader = request.getHeader("Authorization");
            //    if (authHeader != null && authHeader.startsWith("Bearer ")) {
            //        String token = authHeader.substring(7);
            //    }

            // 관례적으로 Bearer라는 문자열을 토큰에 붙여서 전송
            String bearerToken = httpServletRequest.getHeader("Authorization");

            // Token이 없는 경우 검증을 할 수 없으므로, filter chain으로 return
            if (bearerToken == null) {
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }

            // Bearer 문자열을 제거한 후에 JWT 토큰만을 검증
            String token = bearerToken.substring(7);
            System.out.println(token);

            // 3) (보통) 토큰이 존재하면 검증합니다.
            //    - 서명(Signature) 검증
            //    - 만료(Expiration) 확인
            //    - 토큰 위조/변조 여부 확인
            //    - 필요한 클레임(예: userId, email, role) 포함 여부 확인
            //
            //    예)
            //    boolean valid = jwtUtil.validate(token);

            // [Jwts 라이브러리 활용] token 검증 및 claims(payload)추출
            // - parserBuilder : 파싱(디코딩)
            // - setSigningKey : Secret key를 주입시켜 서명부 생성 후 암회화
            // 위 과정을 거쳐 검증을 진행해주는 것
            // - getBody : 파싱하였기때문에 body 뿐만 아니라 header도 꺼내서 사용 가능
            // claims를 기반으로 인증객체를 생성하기 위해 별도의 선언을 진행한것이다.
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(st_secret_key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            System.out.println("claims === " + claims);

            // 4) (보통) 토큰이 유효하면 토큰에서 사용자 식별 정보와 권한(Role)을 파싱합니다.
            //    예)
            //    String userId = jwtUtil.getUserId(token);
            //    List<GrantedAuthority> authorities = jwtUtil.getAuthorities(token);
            // 권한의 경우 다후의 권한을 가질 수 있으므로 일반적으로 List 설계
            List<GrantedAuthority> authorities = new ArrayList<>();
            // 권한을 세팅하는 경우 ROLE_ 접두어를 붙여 추후 권한체크시 사용하는 어노테이션 사용에 용이하도록 설계 (@PreAuthorize 어노테이션)
            authorities.add(new SimpleGrantedAuthority("ROLE_" + claims.get("role")));

            // 5) (보통) 파싱한 정보를 바탕으로 Authentication 객체를 만들고 SecurityContext에 저장합니다.
            //    이렇게 하면 이후 컨트롤러/서비스에서 "인증된 사용자"로 접근할 수 있습니다.
            //
            //    예)
            //    Authentication auth = new UsernamePasswordAuthenticationToken(userId, null, authorities);
            //    SecurityContextHolder.getContext().setAuthentication(auth);

            // claims를 기반으로 Authentication 객체 생성
            // Authentication객체는 SecurityContextHolder의 SecurityContext 객체 내에 위치
            // 1) principal: email, 2) credentials: 필요시 토큰, 3) authorities: 권한 묶음
            Authentication authentication = new UsernamePasswordAuthenticationToken(claims.getSubject(), null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 6) 마지막으로 다음 필터(혹은 최종적으로 DispatcherServlet/컨트롤러)로 요청을 전달해야 합니다.
            //    이 줄을 호출하지 않으면 요청 흐름이 여기서 멈춥니다.

            // 7) (선택) 응답 후처리가 필요하면 doFilter 호출 이후에 작성할 수 있습니다.
            //    예: 로깅, 특정 헤더 추가 등
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 다시 chainFilter로 돌아가는 로직
        filterChain.doFilter(servletRequest, servletResponse); // 현재 체인밖으로 나오지 않고있어서 해당 코드로 다시 filterChain으로 돌아가는 로직을 추가해줌
    }
}

