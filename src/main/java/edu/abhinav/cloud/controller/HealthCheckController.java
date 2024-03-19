package edu.abhinav.cloud.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    Logger logger = (Logger) LogManager.getLogger("WEBAPP_LOGGER");
    Logger infoLogger = (Logger) LogManager.getLogger("WEBAPP_LOGGER_INFO");

    public HealthCheckController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    //GET Mapping--------------------------------------------------------------------------------------------------------
    @GetMapping
    public ResponseEntity<Object> performHealthCheck(@RequestParam(required = false) HashMap<String, String> param, @RequestBody(required = false) String requestBody) {
        try {
            //if param are present or if request body is present return bad request
            if((param.size() > 0) || (requestBody != null && !requestBody.isEmpty())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).cacheControl(CacheControl.noCache()).build();
            }
            //check connection
            Connection connection = dataSource.getConnection();
            connection.close();
            infoLogger.info("Connection Successful");
        } catch(Exception e) {
            logger.error("HealthZ error: " + e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).cacheControl(CacheControl.noCache()).build();
        }
        return ResponseEntity.status(HttpStatus.OK).cacheControl(CacheControl.noCache()).build();
    }

    //POST Mapping-------------------------------------------------------------------------------------------------------
    @PostMapping
    public ResponseEntity<Object> handlePost() {
        logger.error("HealthZ error: Post method not allowed");
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).cacheControl(CacheControl.noCache()).build();
    }

    //DELETE Mapping-----------------------------------------------------------------------------------------------------
    @DeleteMapping
    public ResponseEntity<Object> handleDelete() {
        logger.error("HealthZ error: Delete method not allowed");
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).cacheControl(CacheControl.noCache()).build();
    }

    //PUT Mapping--------------------------------------------------------------------------------------------------------
    @PutMapping
    public ResponseEntity<Object> handlePut() {
        logger.error("HealthZ error: Put method not allowed");
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).cacheControl(CacheControl.noCache()).build();
    }

    //PATCH Mapping------------------------------------------------------------------------------------------------------
    @PatchMapping
    public ResponseEntity<Object> handlePatch() {
        logger.error("HealthZ error: Patch method not allowed");
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).cacheControl(CacheControl.noCache()).build();
    }
}
