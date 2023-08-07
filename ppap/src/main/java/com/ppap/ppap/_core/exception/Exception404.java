package com.ppap.ppap._core.exception;

import com.ppap.ppap._core.utils.ApiUtils;
import org.springframework.http.HttpStatus;

public class Exception404 extends ClientException{
    public Exception404(String message) {super(message);}

    public Exception404(BaseExceptionStatus exception) {
        super(exception.getMessage());
    }

    @Override
    public ApiUtils.ApiResult<?> body() {return ApiUtils.error(getMessage(), HttpStatus.BAD_REQUEST);}

    @Override
    public HttpStatus status() { return HttpStatus.BAD_REQUEST;}
}
