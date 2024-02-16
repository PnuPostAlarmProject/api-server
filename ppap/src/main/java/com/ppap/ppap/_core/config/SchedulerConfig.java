package com.ppap.ppap._core.config;


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
	@SchedulerLog(job="Notice")
	public void run() {
		schedulerService.run();
	}

	@Scheduled(cron = "0 0 23 * * *", zone = "Asia/Seoul")
	@SchedulerLog(job="Csv Reader")
	public void excelJobRun() throws Exception {
		jobLauncher.run(csvReaderJob, new JobParametersBuilder()
			.addLong("uniqueness", System.nanoTime()).toJobParameters());
	}

	@Scheduled(cron = "0 0 1 * * *", zone = "Asia/Seoul")
	@SchedulerLog(job="Fcm Manager")
	public void deviceJobRun() throws Exception {
		jobLauncher.run(manageFcmJob, new JobParametersBuilder()
			.addLong("uniqueness", System.nanoTime()).toJobParameters());
	}
}
