package com.dsi.hackathon.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice(basePackages = {
    "com.dsi.hackathon.controller.mvc"
})
public class MvcExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(MvcExceptionHandler.class);

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ModelAndView handleMvcException(Exception e) {
        logger.error("Exception occurred: ", e);

        return new ModelAndView("error/500");
    }
}
