package com.ppap.ppap._core.utils;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import org.slf4j.MDC;

public class MDCUtils {
	public static class MDCAwareSupplier<T> implements Supplier<T> {
		private final Supplier<T> supplier;
		private final Map<String, String> contextMap;

		public MDCAwareSupplier(Supplier<T> supplier) {
			this.supplier = supplier;
			this.contextMap = MDC.getCopyOfContextMap();
		}

		@Override
		public T get() {
			Map<String, String> oldContext = MDC.getCopyOfContextMap();
			try{
				MDC.setContextMap(contextMap);
				return supplier.get();
			} finally {
				MDC.setContextMap(oldContext);
			}
		}
	}

	public static class MDCAwareFunction<T, R> implements Function<T, R> {
		private final Function<T, R> function;
		private final Map<String, String> contextMap;

		public MDCAwareFunction(Function<T, R> function) {
			this.function = function;
			this.contextMap = MDC.getCopyOfContextMap();
		}

		@Override
		public R apply(T t) {
			Map<String, String> oldContext = MDC.getCopyOfContextMap();
			try{
				MDC.setContextMap(contextMap);
				return function.apply(t);
			} finally {
				MDC.setContextMap(oldContext);
			}
		}
	}
}
