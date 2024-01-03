package com.kakao.rememberBday.domain.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class UserController {

	@Autowired
	private UserService userService;
	
	@RequestMapping(value="/index", method= {RequestMethod.POST, RequestMethod.GET})
	public String index() {
		System.out.println("인덱스 페이지 호출");
		String test = userService.selectTest();
		System.out.println("조회 테스트 : " + test);
		return "index";
	}
	
}
