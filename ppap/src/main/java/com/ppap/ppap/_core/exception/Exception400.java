package com.ppap.ppap._core.exception;

import com.ppap.ppap._core.utils.ApiUtils;
import org.springframework.http.HttpStatus;

public class Exception400 extends ClientException{
    public Exception400(String message) {super(message);}

    public Exception400(BaseExceptionStatus exception) {
        super(exception.getMessage());
    }

    @Override
    public ApiUtils.ApiResult<?> body() {return ApiUtils.error(getMessage(), HttpStatus.BAD_REQUEST);}

    @Override
    public HttpStatus status() { return HttpStatus.BAD_REQUEST;}
}
