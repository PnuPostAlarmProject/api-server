package com.ppap.ppap._core.config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import lombok.RequiredArgsConstructor;

@Profile({"dev", "stage", "prod"})
@Configuration
@RequiredArgsConstructor
public class MailConfig {
	private final Environment env;

	@Bean
	public JavaMailSender javaMailSender() {
		JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

		javaMailSender.setHost(env.getProperty("spring.mail.host"));
		javaMailSender.setPort(Integer.parseInt(env.getProperty("spring.mail.port")));
		javaMailSender.setUsername(env.getProperty("spring.mail.username"));
		javaMailSender.setPassword(env.getProperty("spring.mail.password"));
		javaMailSender.setJavaMailProperties(getProperties());

		return javaMailSender;
	}

	private Properties getProperties() {
		Properties properties = new Properties();
		properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls", "true");
		return properties;
	}
}
