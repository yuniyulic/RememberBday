package com.kakao.rememberBday.global.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KakaoDTO {
	
	private long user_id;
	private String user_name;
	private String user_email;

}
