package com.ppap.ppap._core.exception;

import com.ppap.ppap._core.utils.ApiUtils;
import org.springframework.http.HttpStatus;

public abstract class ClientException extends RuntimeException{

    public ClientException(String message) {super(message);}

    abstract ApiUtils.ApiResult<?> body();

    abstract HttpStatus status();
}
