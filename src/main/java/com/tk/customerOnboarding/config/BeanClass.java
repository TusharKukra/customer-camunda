package com.tk.customerOnboarding.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BeanClass {

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
