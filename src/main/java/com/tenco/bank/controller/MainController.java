package com.tenco.bank.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // IoC 대상(싱글톤 패턴으로 관리) - 제어의 역전 개념
public class MainController {

	// REST API 기반으로 주소 설계 가능
	
	// 주소 설계
	// http://localhost:8080/main-page
	
	@GetMapping({"/main-page", "/index"})
	public String mainPage() {
		System.out.println("mainPage() 호출 확인");
		// [JSP 파일 찾기(yml 설정)] - view resolver
		// prefix: /WEB-INF/view
		//				   /main
		// suffix: .jsp
		return "/main";
	}
	
}
