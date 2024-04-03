package edu.abhinav.cloud.controller;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.abhinav.cloud.service.VerifyUserService;

@Controller
@RequestMapping("/v1")
public class UserVerificationController {

    @Autowired
    private VerifyUserService verifyUserService;

    Logger logger = (Logger) LogManager.getLogger("WEBAPP_LOGGER");
    Logger infoLogger = (Logger) LogManager.getLogger("WEBAPP_LOGGER_INFO");

    @GetMapping("/verify/{username}")
    public ResponseEntity<Object> verifyUser(@RequestParam(required = false) HashMap<String, String> param, @PathVariable String username, @RequestBody(required = false) String userBody) {
        // Check if params or body are present
        if(param.size() > 0 || userBody != null) {
            logger.error("Verify User Error: Params are present or body is null");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).cacheControl(CacheControl.noCache()).build();
        }
        //if username is present, authenticate
        if(username != null) {
            infoLogger.info("Verify User Info: Verifying user status");
            boolean userVerified = verifyUserService.updateStatus(username);
            if (userVerified) {
                infoLogger.info("Verify User Info: User Verified");
                return ResponseEntity.status(HttpStatus.OK).cacheControl(CacheControl.noCache()).body("User Verified Successfully !!");
            } else {
                infoLogger.info("Verify User Info: Link Expired");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).cacheControl(CacheControl.noCache()).body("Link Expired...");
            }
        }
        else {
            logger.error("Verify User Error : Username is not present in query");
        }
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).cacheControl(CacheControl.noCache()).build();
    }
}