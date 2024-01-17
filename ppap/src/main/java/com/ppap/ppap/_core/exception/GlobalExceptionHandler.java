package com.ppap.ppap._core.exception;

import com.ppap.ppap._core.utils.ApiUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<?> dtoValidationException(MethodArgumentNotValidException e) {
        List<ObjectError> errors = e.getBindingResult().getAllErrors();
        return new ResponseEntity<>(ApiUtils.error(errors.get(0).getDefaultMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> inValidInput(Exception e) {
        ApiUtils.ApiResult<?> apiResult = ApiUtils.error("정상적인 입력이 아닙니다.", HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(apiResult, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<?> dtoTypeMismatchException(Exception e) {
        return new ResponseEntity<>(ApiUtils.error("입력의 타입이 올바르지 않습니다.", HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({Exception400.class, Exception401.class, Exception403.class, Exception404.class})
    public ResponseEntity<?> clientException(ClientException e) {
        return new ResponseEntity<>(e.body(), e.status());
    }

    @ExceptionHandler({Exception500.class})
    public ResponseEntity<?> serverException(ServerException e) {
        //logging 추가해주는 것이 좋아보임

        return new ResponseEntity<>(e.body(), e.status());
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<?> unknownServerError(Exception e) {
        // logging 추가
        log.error(e.getMessage());
        return new ResponseEntity<>(
                ApiUtils.error("서버에서 알 수 없는 에러가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
