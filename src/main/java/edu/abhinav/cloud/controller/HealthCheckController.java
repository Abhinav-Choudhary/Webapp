package edu.abhinav.cloud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.util.HashMap;

import javax.sql.DataSource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;



@RestController
@RequestMapping("/healthz")
public class HealthCheckController {

    private final DataSource dataSource;

    public HealthCheckController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping
    public ResponseEntity<Object> performHealthCheck(@RequestParam(required = false) HashMap<String, String> param, @RequestBody(required = false) String requestBody) {
        try {
            if((param.size() > 0) || (requestBody != null && !requestBody.isEmpty())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).cacheControl(CacheControl.noCache()).build();
            }

            Connection connection = dataSource.getConnection();
            connection.close();
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).cacheControl(CacheControl.noCache()).build();
        }
        return ResponseEntity.status(HttpStatus.OK).cacheControl(CacheControl.noCache()).build();
    }

    @PostMapping
    public ResponseEntity<Object> handlePost() {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).cacheControl(CacheControl.noCache()).build();
    }

    @DeleteMapping
    public ResponseEntity<Object> handleDelete() {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).cacheControl(CacheControl.noCache()).build();
    }

    @PutMapping
    public ResponseEntity<Object> handlePut() {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).cacheControl(CacheControl.noCache()).build();
    }

    @PatchMapping
    public ResponseEntity<Object> handlePatch() {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).cacheControl(CacheControl.noCache()).build();
    }
    
}
