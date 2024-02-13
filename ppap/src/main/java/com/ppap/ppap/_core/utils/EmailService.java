package com.ppap.ppap._core.utils;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.ppap.ppap.domain.subscribe.entity.Notice;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@Profile({"dev", "stage", "prod"})
@RequiredArgsConstructor
public class EmailService {
	private final JavaMailSender mailSender;

	@Value("${spring.mail.username}")
	private String fromEmail;

	@Value("${spring.mail.admin}")
	private String adminEmail;

	public void sendEmailToAdmin(final String text) throws MessagingException {

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);

		helper.setFrom(String.format("PPAP <%s>", fromEmail));
		helper.setText(text);
		helper.setSubject("FCM 토큰 처리 중 문제가 발생했습니다.");
		CompletableFuture.runAsync(() -> mailSender.send(message), Executors.newSingleThreadExecutor());
	}

	public void sendEmailForSchdulerErrorLog(Map<Notice, String> errorNotices, final String toEmail) throws MessagingException {

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);

		helper.setFrom(String.format("PPAP <%s>", fromEmail));
		helper.setTo(toEmail);

		String text = getHtmlMessage(errorNotices);
		helper.setSubject("스케줄러 동작 중 에러가 발생했습니다.");
		helper.setText(text);
		CompletableFuture.runAsync(() -> mailSender.send(message), Executors.newSingleThreadExecutor());
	}



	private String getHtmlMessage(Map<Notice, String> errorNotices) {
		StringBuilder sb = new StringBuilder();
		sb.append("오류 공지사항 안내\n");

		Set<Notice> sortedNotice = new TreeSet<>(Comparator.comparing(Notice::getId));
		sortedNotice.addAll(errorNotices.keySet());

		sortedNotice.forEach( notice -> {
			sb.append(String.format("공지사항 ID: %d\n", notice.getId()));
			sb.append(String.format("공지사항 링크 : %s\n", notice.getLink()));
			sb.append(String.format("에러 이유 : %s\n", errorNotices.get(notice)));
			sb.append("======================\n");
		});

		return sb.toString();
	}
}
