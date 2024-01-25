package com.ppap.ppap._core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.ppap.ppap._core.scheduler.SchedulerService;

@Configuration
@EnableScheduling
@Profile({"dev", "stage", "prod"})
public class SchedulerConfig {

	private final SchedulerService schedulerService;

	@Autowired
	public SchedulerConfig(SchedulerService schedulerService) {
		this.schedulerService = schedulerService;
	}

	@Scheduled(cron = "0 0/10 * * * ?", zone="Asia/Seoul")
	public void run() {
		schedulerService.run();
	}

}
