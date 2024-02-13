package com.ppap.ppap._core.scheduler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) // 메소드에만 사용할 수 있도록 설정
@Retention(RetentionPolicy.RUNTIME) // 런타임에도 어노테이션 정보 유지
public @interface SchedulerLog {

}
