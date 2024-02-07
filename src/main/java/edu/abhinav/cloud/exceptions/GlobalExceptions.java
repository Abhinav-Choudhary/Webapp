package edu.abhinav.cloud.exceptions;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptions {
    
    @ExceptionHandler
    public ResponseEntity<Object> handleInvalidPaths(Exception e) {
        System.out.println(e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).cacheControl(CacheControl.noCache()).build();
    }
    
}
