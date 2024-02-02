package com.ppap.ppap._core.filter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Objects;

import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@WebFilter(urlPatterns = "/*")
public class LoggingFilter implements OrderedFilter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws
		IOException,
		ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(httpRequest);
		ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(httpResponse);

		chain.doFilter(requestWrapper, responseWrapper);

		if (isSuccessRequest(httpResponse)) {
			log.info("\n{} - {} {} {} \nREQUEST Body : {}\n",
				httpRequest.getMethod(),
				httpRequest.getRequestURI(),
				httpRequest.getHeader("X-FORWARDED-FOR") == null ?
					httpRequest.getRemoteAddr() : httpRequest.getHeader("X-FORWARDED-FOR"),
				httpResponse.getStatus(),
				getRequestBody(requestWrapper));
		} else {
			log.info("\n{} - {} {} {} \nREQUEST Body : {}\nRESPONSE Body : {}\n",
				httpRequest.getMethod(),
				httpRequest.getRequestURI(),
				httpRequest.getHeader("X-FORWARDED-FOR") == null ?
					httpRequest.getRemoteAddr() : httpRequest.getHeader("X-FORWARDED-FOR"),
				httpResponse.getStatus(),
				getRequestBody(requestWrapper),
				getResponseBody(responseWrapper));
		}

		responseWrapper.copyBodyToResponse();
	}

	private String getRequestBody(ContentCachingRequestWrapper requestWrapper) {
		ContentCachingRequestWrapper request = WebUtils.getNativeRequest(requestWrapper, ContentCachingRequestWrapper.class);
		if (Objects.nonNull(request)) {
			try {
				return new String(request.getContentAsByteArray(), request.getCharacterEncoding());
			} catch (UnsupportedEncodingException ignore) {
			}
		}
		return " - ";
	}
	private String getResponseBody(ContentCachingResponseWrapper responseWrapper) {
		ContentCachingResponseWrapper response = WebUtils.getNativeResponse(responseWrapper, ContentCachingResponseWrapper.class);
		if (Objects.nonNull(response)) {
			try{
				return new String(response.getContentAsByteArray(), response.getCharacterEncoding());
			} catch (UnsupportedEncodingException ignore) {

			}
		}
		return " - ";
	}
	private boolean isSuccessRequest(HttpServletResponse httpResponse) {
		return httpResponse.getStatus() >= 200 && httpResponse.getStatus() <= 399;
	}

	@Override
	public int getOrder() {
		return OrderedFilter.HIGHEST_PRECEDENCE+1;
	}
}
