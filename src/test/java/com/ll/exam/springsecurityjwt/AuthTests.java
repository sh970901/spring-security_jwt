package com.ll.exam.springsecurityjwt;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc //Mockmvc를 쓸 수 있게 해준다.
@Transactional
@ActiveProfiles("test") //applicationContext에게 test profile을 활성화 하라고 알려줌
class AuthTests {
	@Autowired
	private MockMvc mvc;

	@Test
	@DisplayName("POST /member/login 은 로그인 처리 URL 이다.")
	void t1() throws Exception {
		// When
		ResultActions resultActions = mvc
				.perform(
						post("/member/login")
								.content("""
                                        {
                                            "username": "user1",
                                            "password": "1234"
                                        }
                                        """.stripIndent())
								.contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)) //json으로 보냄
				)
				.andDo(print());

		// Then
		resultActions
				.andExpect(status().is2xxSuccessful());
	}
}
