package com.github.fenixsoft.bookstore.infrastructure.jaxrs;


import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.ConstraintViolationException;
import java.util.List;


/**
 * @author huf
 */
@ControllerAdvice
@Component
public class GlobalExceptionAdvice {

    @ExceptionHandler(value = {WebExchangeBindException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResponse handle(WebExchangeBindException e) {

        List<ObjectError> allErrors = e.getBindingResult().getAllErrors();

        if(allErrors.size()==0){
            return CommonResponse.ok();
        }
        String defaultMessage = allErrors.get(0).getDefaultMessage();
        if(StringUtils.isEmpty(defaultMessage)){
            return CommonResponse.ok();
        }
        return CommonResponse.failure(defaultMessage);


    }

    @ExceptionHandler(value = {ConstraintViolationException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResponse handle(ConstraintViolationException e) {
        return CommonResponse.failure(e.getMessage());

    }

    @ExceptionHandler(value = {ResponseStatusException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResponse handle(ResponseStatusException e) {
        return CommonResponse.failure(e.getReason());
    }

    @ExceptionHandler(value = {Exception.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResponse handle(Exception e) {
        return CommonResponse.failure(e.getMessage());
    }

    @ExceptionHandler(value = {UnsupportedOperationException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResponse handle(UnsupportedOperationException e) {
        return CommonResponse.failure(e.getMessage());
    }
}
