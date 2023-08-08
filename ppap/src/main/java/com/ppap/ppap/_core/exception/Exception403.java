package com.ppap.ppap._core.exception;

import com.ppap.ppap._core.utils.ApiUtils;
import org.springframework.http.HttpStatus;

public class Exception403 extends ClientException{
    public Exception403(String message) {super(message);}

    public Exception403(BaseExceptionStatus exception) {
        super(exception.getMessage());
    }

    @Override
    public ApiUtils.ApiResult<?> body() {return ApiUtils.error(getMessage(), HttpStatus.FORBIDDEN);}

    @Override
    public HttpStatus status() { return HttpStatus.FORBIDDEN;}
}
