package com.kakao.rememberBday;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JasyptConfigTest {
	@Test
	void jasypt() {
		String secret = "암호화하려는 값";
		
		System.out.printf("%s -> $s", secret, jasyptEncoding(secret));
	}
	
	public String jasyptEncoding(String value) {
		String key = "key";
		StandardPBEStringEncryptor pbeEnc = new StandardPBEStringEncryptor();
		pbeEnc.setAlgorithm("PBEWithMD5AndDES");
		pbeEnc.setPassword(key);
		return pbeEnc.encrypt(value);
	}
}
