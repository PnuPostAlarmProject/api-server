package com.ppap.ppap._core.exception;

import com.ppap.ppap._core.utils.ApiUtils;
import org.springframework.http.HttpStatus;

public class Exception500 extends ServerException{
    public Exception500(String message) {super(message);}

    public Exception500(BaseExceptionStatus exception) {
        super(exception.getMessage());
    }

    @Override
    public ApiUtils.ApiResult<?> body() {return ApiUtils.error(getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);}

    @Override
    public HttpStatus status() { return HttpStatus.INTERNAL_SERVER_ERROR;}
}