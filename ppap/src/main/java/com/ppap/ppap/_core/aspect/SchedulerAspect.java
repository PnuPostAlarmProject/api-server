package com.ppap.ppap._core.aspect;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import com.ppap.ppap._core.scheduler.SchedulerLog;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class SchedulerAspect {

	@Around("@annotation(schedulerLog)")
	public Object loggingTask(ProceedingJoinPoint joinPoint, SchedulerLog schedulerLog) throws Throwable {

		MDC.put("logFileName", "schedule");
		log.info("{} Scheduler Start", schedulerLog.job());
		long start = System.currentTimeMillis();

		try {
			Object proceed = joinPoint.proceed();
			log.info("실행시간 : {} ms", System.currentTimeMillis() - start);
			log.info("{} Scheduler End", schedulerLog.job());
			MDC.clear();
			return proceed;
		} catch (Exception e) {
			log.error(ExceptionUtils.getStackTrace(e));
			log.info("실행시간 : {} ms", System.currentTimeMillis() - start);
			log.info("{} Scheduler End", schedulerLog.job());
			MDC.clear();
			throw e;
		}
	}
}
