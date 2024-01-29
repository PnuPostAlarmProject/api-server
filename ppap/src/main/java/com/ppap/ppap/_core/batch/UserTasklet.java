package com.ppap.ppap._core.batch;

import java.util.UUID;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ppap.ppap.domain.user.entity.User;
import com.ppap.ppap.domain.user.entity.constant.Provider;
import com.ppap.ppap.domain.user.entity.constant.Role;
import com.ppap.ppap.domain.user.repository.UserJpaRepository;

public class UserTasklet implements Tasklet {
	private final UserJpaRepository userJpaRepository;
	private final PasswordEncoder passwordEncoder;

	public UserTasklet(UserJpaRepository userJpaRepository, PasswordEncoder passwordEncoder) {
		this.userJpaRepository = userJpaRepository;
		this.passwordEncoder = passwordEncoder;
	}

	private static final String BATCH_USER = "ppap_notice@notice.com";
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

		userJpaRepository.findByEmail(BATCH_USER).orElseGet(
			() -> userJpaRepository.save(User.builder()
					.email(BATCH_USER)
					.password(passwordEncoder.encode(UUID.randomUUID().toString()))
					.role(Role.ROLE_BATCH)
					.provider(Provider.PROVIDER_NORMAL)
				.build()));
		return RepeatStatus.FINISHED;
	}
}
