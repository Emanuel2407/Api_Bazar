package com.bazar.apibazar;

import com.bazar.apibazar.bootstrap.SystemBootstrap;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
class ApibazarApplicationTests {

	@MockitoBean
	private SystemBootstrap systemBootstrap;

	@Test
	void contextLoads() {
	}

}
