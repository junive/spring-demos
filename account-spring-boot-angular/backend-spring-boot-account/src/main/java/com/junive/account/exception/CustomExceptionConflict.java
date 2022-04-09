package com.junive.account.exception;


import com.junive.account.util.CustomStatus;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Builder
@Getter
@ResponseStatus(HttpStatus.CONFLICT)
public class CustomExceptionConflict extends RuntimeException {
    private Object badValue;
    private CustomStatus customStatus;
}
