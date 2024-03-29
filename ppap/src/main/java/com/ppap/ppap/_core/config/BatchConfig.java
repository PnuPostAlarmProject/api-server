package com.ppap.ppap._core.config;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.sql.DataSource;


import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;

import com.ppap.ppap._core.batch.DeviceRowMapper;
import com.ppap.ppap._core.batch.DeviceWriter;
import com.ppap.ppap._core.batch.NoticeWriter;
import com.ppap.ppap._core.batch.UserTasklet;
import com.ppap.ppap._core.firebase.FcmTokenValidator;
import com.ppap.ppap._core.utils.EmailService;
import com.ppap.ppap.domain.subscribe.dto.NoticeDto;
import com.ppap.ppap.domain.subscribe.entity.constant.NoticeType;
import com.ppap.ppap.domain.subscribe.repository.NoticeJpaRepository;
import com.ppap.ppap.domain.subscribe.repository.SubscribeJpaRepository;
import com.ppap.ppap.domain.subscribe.repository.UnivJpaRepository;
import com.ppap.ppap.domain.user.entity.Device;
import com.ppap.ppap.domain.user.repository.DeviceJpaRepository;
import com.ppap.ppap.domain.user.repository.UserJpaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Profile({"dev", "stage", "prod"})
@Configuration
public class BatchConfig {

	private final DataSource dataSource;
	private final UserJpaRepository userJpaRepository;
	private final PasswordEncoder passwordEncoder;
	private final NoticeJpaRepository noticeJpaRepository;
	private final UnivJpaRepository univJpaRepository;
	private final SubscribeJpaRepository subscribeJpaRepository;
	private final FcmTokenValidator fcmTokenValidator;
	private final DeviceJpaRepository deviceJpaRepository;
	private final EmailService emailService;

	private final int CHUNK_SIZE = 100;

	@Bean(name = "manageFcmDeviceJob")
	public Job manageFcmdeviceJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new JobBuilder("manageFcmDeviceJob", jobRepository)
			.incrementer(new RunIdIncrementer())
			.start(fcmDeviceChunkStep(jobRepository, transactionManager))
			.build();
	}

	@Bean(name = "csvReaderJob")
	public Job csvReaderJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new JobBuilder("csvReaderJob", jobRepository)
			.incrementer(new RunIdIncrementer())
			.start(userStep(jobRepository, transactionManager))
			.next(chunkStep(jobRepository, transactionManager))
			.build();
	}

	@Bean
	public Step userStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("batchUserCheckStep", jobRepository)
			.tasklet(new UserTasklet(userJpaRepository, passwordEncoder), transactionManager)
			.build();
	}

	@Bean
	public Step chunkStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("csvReadStep", jobRepository)
			.<NoticeDto, NoticeDto>chunk(CHUNK_SIZE, transactionManager)
			.reader(reader())
			.writer(writer())
			.build();
	}

	@Bean
	public Step fcmDeviceChunkStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("manageFcmStep", jobRepository)
			.<Device, Device>chunk(CHUNK_SIZE, transactionManager)
			.reader(deviceReader())
			.writer(deviceWriter())
			.build();
	}


	@Bean
	public FlatFileItemReader<NoticeDto> reader() {
		return new FlatFileItemReaderBuilder<NoticeDto>()
			.name("csvReader")
			.resource(new FileSystemResource("./src/main/resources/notice_link.csv"))
			.delimited() // 구분자로 나뉜 데이터가 들어감
			.names(new String[]{"college", "department", "type", "title", "link", "noticeType"})
			.fieldSetMapper(fieldSet -> NoticeDto.builder()
				.college(fieldSet.readString("college"))
				.department(fieldSet.readString("department"))
				.noticeType(NoticeType.valueOf(fieldSet.readString("noticeType")))
				.link(fieldSet.readString("link"))
				.title(fieldSet.readString("title"))
				.lastNoticeTime(getLastNoticeTime(fieldSet.readString("noticeType")))
				.build())
			.linesToSkip(1)
			.build();
	}

	@Bean
	public NoticeWriter writer() {
		return new NoticeWriter(
			userJpaRepository,
			noticeJpaRepository,
			univJpaRepository,
			subscribeJpaRepository
		);
	}

	@Bean
	public JdbcCursorItemReader<Device> deviceReader() {
		return new JdbcCursorItemReaderBuilder<Device>()
			.name("jdbcCursorDeviceReader")
			.fetchSize(CHUNK_SIZE)
			.dataSource(dataSource)
			.rowMapper((rs, rowNum) ->
				Device.builder()
					.id(rs.getLong("device_id"))
					.fcmToken(rs.getString("fcm_token"))
					.build())
			.sql("SELECT device_id, fcm_token FROM device_tb")
			.build();
	}
	
	@Bean
	public DeviceWriter deviceWriter() {
		return new DeviceWriter(
			deviceJpaRepository,
			fcmTokenValidator,
			emailService
		);
	}


	private LocalDateTime getLastNoticeTime(String isRss) {
		if (isRss.equals("Y"))
			return LocalDateTime.now();
		return LocalDate.now().atStartOfDay();
	}
}
