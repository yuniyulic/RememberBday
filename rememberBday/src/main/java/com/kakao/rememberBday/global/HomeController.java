package com.kakao.rememberBday.global;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.kakao.rememberBday.global.auth.KakaoService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class HomeController {

//	private final KakaoService kakaoService;
	
	@Autowired
	KakaoService kakaoService;
	
	@RequestMapping(value="/", method=RequestMethod.GET)
	public String login(Model model) {
		model.addAttribute("kakaoUrl", kakaoService.getKakaoLogin());
		
		//test@@@@@@@@@@@@@@@@@@@@@
		System.out.println("kakaoUrl : " + model.getAttribute("kakaoUrl"));
		
		return "index";
	}
	
	@GetMapping("/main")
		public String homePage(Model model) {
		return "main";
	}
	
}
