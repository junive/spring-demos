package com.junive.account.exception;

import com.junive.account.util.CustomStatus;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Builder
@Getter
@ResponseStatus(HttpStatus.NOT_FOUND)
public class CustomExceptionNotFound extends RuntimeException {
    protected String badValue;
    protected CustomStatus customStatus;
}
