package com.beyond.basic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * =========================================================
 * BasicApplication
 * =========================================================
 *
 * [부트스트랩(시작점) 코드]
 * - 이 클래스는 프로젝트를 실행시키는 “시작점(main)”이다.
 * - 목적은 애플리케이션을 기동(서버 실행)하는 것이며,
 *   여기에는 특정 비즈니스 로직을 넣고 실행하는 용도가 아니다.
 *
 * [요청 처리 관점에서의 핵심]
 * - 서버가 뜨면 사용자의 HTTP 요청이 들어온다는 전제를 가져야 한다.
 * - 요청은 브라우저/클라이언트에서 들어오고,
 *   스프링 내부에서는 DispatcherServlet을 통해 컨트롤러로 라우팅되며
 *   컨트롤러 → 서비스 → 레포지토리 순으로 처리 흐름이 이어진다.
 * - 즉 “요청이 어디서 들어오고, 어디서부터 어떤 순서로 처리되는가”를 항상 염두에 둔다.
 *
 * [@SpringBootApplication]
 * - 스프링 부트 애플리케이션의 핵심 설정 어노테이션이다.
 * - 내부적으로 컴포넌트 스캔이 동작하여,
 *   이 클래스의 패키지 하위 경로에 있는 컴포넌트들을 스캔하고 빈으로 등록해 관리한다. (singleton)
 */
@SpringBootApplication
public class BasicApplication {

	public static void main(String[] args) {
		SpringApplication.run(BasicApplication.class, args);
	}

}
