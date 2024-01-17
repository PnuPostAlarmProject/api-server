package com.ppap.ppap._core.filter;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.AbstractMatcherFilter;
import ch.qos.logback.core.spi.FilterReply;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MDCFilterRegular extends AbstractMatcherFilter<ILoggingEvent> {
	String MDCKey;
	String Value;

	public MDCFilterRegular() {
	}

	public void start() {
		if (this.MDCKey != null && this.Value != null)
			super.start();
	}

	@Override
	public FilterReply decide(ILoggingEvent event) {
		if (!event.getMDCPropertyMap().containsKey(this.MDCKey)) {
			return this.onMismatch;
		}

		return event.getMDCPropertyMap().get(this.MDCKey).equals(Value) ? this.onMatch : this.onMismatch;
	}
}
