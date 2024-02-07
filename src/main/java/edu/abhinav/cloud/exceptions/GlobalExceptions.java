package edu.abhinav.cloud.exceptions;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptions {
    
    // If any unhandled exception occurs, return Not Found status code
    @ExceptionHandler
    public ResponseEntity<Object> handleInvalidPaths(Exception e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).cacheControl(CacheControl.noCache()).build();
    }
    
}
