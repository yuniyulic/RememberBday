package com.kakao.rememberBday.global.auth;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("kakao")
public class KakaoController {

	@Autowired
	KakaoService kakaoService;

//	@GetMapping("callback")
//	public ResponseEntity<MsgEntity> callback(HttpServletRequest request) throws Exception {
//		HashMap<String, Object> kakaoInfo = kakaoService.getKakaoInfo(request.getParameter("code"));
//		
//		System.out.println("/kakao/callback 성공");
//		
//		return ResponseEntity.ok().body(new MsgEntity("Success", kakaoInfo));
//	}
	
//	@RequestMapping("callback")
	@RequestMapping(value="/callback", method= {RequestMethod.POST, RequestMethod.GET})
	public String callback(HttpServletRequest request, Model model) throws Exception {
		HashMap<String, Object> kakaoInfo = kakaoService.getKakaoInfo(request.getParameter("code"));
		
		System.out.println("/kakao/callback 성공");
		System.out.println("kakaoInfo: " + kakaoInfo);
	
		
		// 모델에 데이터 추가
		model.addAttribute("kakaoInfo", kakaoInfo);
		
		// 캘린더 호출
		String accessToken = kakaoService.getAccessTokenByUserId();
		kakaoService.getCalendars(accessToken);
		
		ResponseEntity<String> calendarsResponse = kakaoService.getCalendars(accessToken);
		
		// 응답 상태 코드 확인
		int statusCode = calendarsResponse.getStatusCodeValue();
		System.out.println("Status Code: " + statusCode);
	
		// 응답 헤더 확인
		HttpHeaders headers = calendarsResponse.getHeaders();
		System.out.println("Headers: " + headers);
	
		// 응답 본문(body) 확인
		String responseBody = calendarsResponse.getBody();
		System.out.println("Response Body: " + responseBody);
		model.addAttribute("responseBody", responseBody);
		
		// main.html로 이동
	//		return "<script>location.href='/main';</script>";
		return "main";
	}
	
	@GetMapping("/getAccessTokenByUserId")
	public String getAccessTokenByUserId() throws Exception {

		// KakaoService의 getCalendars 메소드 호출
		return kakaoService.getAccessTokenByUserId();
	}
	
}
