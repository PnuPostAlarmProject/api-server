package com.ppap.ppap._core.exception;

import com.ppap.ppap._core.utils.ApiUtils;
import org.springframework.http.HttpStatus;

public class Exception401 extends ClientException{
    public Exception401(String message) {super(message);}

    @Override
    public ApiUtils.ApiResult<?> body() {return ApiUtils.error(getMessage(), HttpStatus.UNAUTHORIZED);}

    @Override
    public HttpStatus status() { return HttpStatus.UNAUTHORIZED;}
}
