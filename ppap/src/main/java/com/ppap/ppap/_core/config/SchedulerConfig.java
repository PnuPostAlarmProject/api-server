package com.ppap.ppap._core.config;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.MDC;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.ppap.ppap._core.scheduler.SchedulerLog;
import com.ppap.ppap._core.scheduler.SchedulerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableScheduling
@Profile({"dev", "stage", "prod"})
@Slf4j
public class SchedulerConfig {
	private final JobLauncher jobLauncher;

	private final Job csvReaderJob;

	private final Job manageFcmJob;
	private final SchedulerService schedulerService;

	@Autowired
	public SchedulerConfig(JobLauncher jobLauncher, @Qualifier("csvReaderJob") Job csvReaderJob, @Qualifier("manageFcmDeviceJob") Job manageFcmJob,
		SchedulerService schedulerService) {
		this.jobLauncher = jobLauncher;
		this.csvReaderJob = csvReaderJob;
		this.manageFcmJob = manageFcmJob;
		this.schedulerService = schedulerService;
	}

	@Scheduled(cron = "0 0/30 9-22 * * *", zone="Asia/Seoul")
	@SchedulerLog
	public void run() {
		log.info("Notice Scheduler Start");
		long start = System.currentTimeMillis();
		try {
			schedulerService.run();
		} finally{
			log.info("실행시간 : {} ms", System.currentTimeMillis() - start);
			log.info("Notice Scheduler end");
		}
	}

	@Scheduled(cron = "0 0 23 * * *", zone = "Asia/Seoul")
	@SchedulerLog
	public void excelJobRun() {
		log.info("Csv Reader Scheduler Start");
		long start = System.currentTimeMillis();
		try {
			jobLauncher.run(csvReaderJob, new JobParametersBuilder()
				.addLong("uniqueness", System.nanoTime()).toJobParameters());
		} catch (Exception e) {
			log.error(ExceptionUtils.getStackTrace(e));
		} finally{
			log.info("실행시간 : {} ms", System.currentTimeMillis() - start);
			log.info("Csv Reader Scheduler end");
		}
	}

	@Scheduled(cron = "0 0 1 * * *", zone = "Asia/Seoul")
	@SchedulerLog
	public void deviceJobRun() {
		log.info("Fcm Manager Scheduler Start");
		long start = System.currentTimeMillis();
		try {
			jobLauncher.run(manageFcmJob, new JobParametersBuilder()
				.addLong("uniqueness", System.nanoTime()).toJobParameters());
		} catch (Exception e) {
			log.error(ExceptionUtils.getStackTrace(e));
		} finally{
			log.info("실행시간 : {} ms", System.currentTimeMillis() - start);
			log.info("csv Reader Scheduler end");
		}
	}
}
