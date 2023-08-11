package com.ppap.ppap._core.exception;

import com.ppap.ppap._core.utils.ApiUtils;
import org.springframework.http.HttpStatus;

public class Exception502 extends ServerException{
    public Exception502(String message) {super(message);}

    public Exception502(BaseExceptionStatus exception) {
        super(exception.getMessage());
    }

    @Override
    public ApiUtils.ApiResult<?> body() {return ApiUtils.error(getMessage(), HttpStatus.BAD_GATEWAY);}

    @Override
    public HttpStatus status() { return HttpStatus.BAD_GATEWAY;}
}
