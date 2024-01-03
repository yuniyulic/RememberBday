package com.kakao.rememberBday.global.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("kakao")
public class KakaoController {

	@Autowired
	KakaoService kakaoService;

	@GetMapping("callback")
	public ResponseEntity<MsgEntity> callback(HttpServletRequest request) throws Exception {
		KakaoDTO kakaoInfo = kakaoService.getKakaoInfo(request.getParameter("code"));
		
		return ResponseEntity.ok().body(new MsgEntity("Success", kakaoInfo));
	}
	
}
