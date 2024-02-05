package com.ppap.ppap._core.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.ppap.ppap._core.scheduler.SchedulerService;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableScheduling
@Profile({"dev", "stage", "prod"})
public class SchedulerConfig {
	private final JobLauncher jobLauncher;
	private final Job csvReaderJob;
	private final SchedulerService schedulerService;

	@Scheduled(cron = "0 0/30 9-22 * * *", zone="Asia/Seoul")
	public void run() {
		schedulerService.run();
	}

	@Scheduled(cron = "0 0 23 * * *", zone = "Asia/Seoul")
	public void batchJobRun() throws Exception{
		jobLauncher.run(csvReaderJob, new JobParametersBuilder()
			.addLong("uniqueness", System.nanoTime()).toJobParameters());
	}
}
