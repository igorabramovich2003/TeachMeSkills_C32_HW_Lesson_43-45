package com.tms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class AppExceptionHandler {

    @ExceptionHandler(value = AgeException.class)
    public ModelAndView ageExceptionHandler(AgeException exception) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("innerError");
        modelAndView.addObject("message", exception.getMessage());
        modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        return modelAndView;
    }

    @ExceptionHandler(value = Exception.class)
    public ModelAndView allExceptionsHandler(Exception exception) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("error");
        modelAndView.addObject("message", exception.getMessage());
        modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        return modelAndView;
    }
}
