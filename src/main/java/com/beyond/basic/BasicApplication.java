package com.beyond.basic;

import com.beyond.basic.b1_basic.Member;
import com.beyond.basic.b1_basic.MemberController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// 아래의 코드가 프로젝트 실행코드(시작점)로 <부트스트랩 코드:프로젝트 시작 용도의 코드> 라고도 부른다.
// 해당 코드의 목적은 프로젝트를 실행시키기 위한 목적일 뿐이다. 즉, 코드안에 특정한 로직을 넣고 수행하는 목적이 아니다.
// 이 때 사용자의 요청이 반드시 들어온다고 생각해야하고 사용자의 요청이 어디서 들어오게 되는지 생각해야함.
// 사용자의 요청이 어디서 들어오고, 어디서부터 순차적으로 처리해야하지?
@SpringBootApplication
public class BasicApplication {

	public static void main(String[] args) {
		SpringApplication.run(BasicApplication.class, args);
	}

}
