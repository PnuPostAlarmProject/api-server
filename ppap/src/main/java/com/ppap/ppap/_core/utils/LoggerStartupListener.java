package com.ppap.ppap._core.utils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggerContextListener;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.LifeCycle;

public class LoggerStartupListener extends ContextAwareBase implements LoggerContextListener, LifeCycle {
	private boolean started = false;

	@Override
	public void start() {
		if (started) return;
		String username = System.getenv("GMAIL_USER");
		String password = System.getenv("GMAIL_PASSWORD");
		String adminEmail = System.getenv("ADMIN_EMAIL");

		Context context = getContext();
		context.putProperty("GMAIL_USER", username);
		context.putProperty("GMAIL_PASSWORD", password);
		context.putProperty("ADMIN_EMAIL", adminEmail);
		started = true;
	}

	@Override
	public boolean isResetResistant() {
		return true;
	}

	@Override
	public void onStart(LoggerContext context) {

	}

	@Override
	public void onReset(LoggerContext context) {

	}

	@Override
	public void onStop(LoggerContext context) {

	}

	@Override
	public void onLevelChange(Logger logger, Level level) {

	}

	@Override
	public void stop() {

	}

	@Override
	public boolean isStarted() {
		return started;
	}
}
