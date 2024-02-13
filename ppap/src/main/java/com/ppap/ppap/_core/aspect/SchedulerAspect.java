package com.ppap.ppap._core.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class SchedulerAspect {

	@Around("@annotation(com.ppap.ppap._core.scheduler.SchedulerLog)")
	public Object loggingTask(ProceedingJoinPoint joinPoint) throws Throwable{
		MDC.put("logFileName", "schedule");
		Object proceed = joinPoint.proceed();
		MDC.clear();
		return proceed;
	}
}
