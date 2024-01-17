package com.ppap.ppap._core.filter;

import java.io.IOException;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.stereotype.Component;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class ApiMdcFilter implements OrderedFilter {
	@Override
		public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws
		IOException,
			ServletException {
			// String requestId = ((HttpServletRequest) request).getHeader("X-RequestID");
			MDC.put("logFileName", "api");
			// MDC.put(requestId, StringUtils.defaultString(requestId, UUID.randomUUID().toString().replaceAll("-", "")));
			chain.doFilter(request, response);
			MDC.clear();
		}

		@Override
		public int getOrder() {
			return OrderedFilter.HIGHEST_PRECEDENCE;
	}
}
