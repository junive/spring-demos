package com.junive.account.exception;

import com.junive.account.util.ServletUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
@RequiredArgsConstructor
public class ExceptionHandlerController {

    private void printError(RuntimeException ex) {
        System.out.println("ERROR CLASS : "+ ex.getStackTrace()[1].getClassName()
                + " AND METHOD : " +ex.getStackTrace()[1].getMethodName());
    }


    @ExceptionHandler(CustomExceptionNotFound.class)
    public final void handleNotFound(CustomExceptionNotFound ex,
                                     HttpServletRequest request,
                                     HttpServletResponse response) {
        this.printError(ex);
        //return new ResponseEntity(error, HttpStatus.NOT_FOUND);
        ServletUtil.errorResponse(request, response,
                HttpStatus.NOT_FOUND,
                ex.getCustomStatus(),
                ex.getBadValue());
    }

    @ExceptionHandler(CustomExceptionInvalid.class)
    public final void handleInvalid(CustomExceptionInvalid ex,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {
        this.printError(ex);
        ServletUtil.errorResponse(request, response,
                HttpStatus.NOT_ACCEPTABLE,
                ex.getCustomStatus(),
                ex.getBadValue());
    }

    @ExceptionHandler(CustomExceptionConflict.class)
    public final void handleConflict( CustomExceptionConflict ex,
                                          HttpServletRequest request,
                                          HttpServletResponse response) {
        this.printError(ex);
        ServletUtil.errorResponse(request, response,
                HttpStatus.CONFLICT,
                ex.getCustomStatus(),
                ex.getBadValue());
    }

}
