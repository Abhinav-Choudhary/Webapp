package edu.abhinav.cloud.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.abhinav.cloud.pojo.User;
import edu.abhinav.cloud.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/v1")
public class UserController {

    @Autowired
    UserService userService;
    
    @GetMapping("/user/self")
    public ResponseEntity<Object> getUserDetails(@RequestParam(required = false) String param, @RequestHeader(required = false) HttpHeaders headers, @RequestBody(required = false) String userBody) {
        String[] userCreds = authorizeUser(headers);
        boolean checkUserPassword = userService.authenticateUser(userCreds[0], userCreds[1]);
        if(!checkUserPassword) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).cacheControl(CacheControl.noCache()).build();
        }
        return ResponseEntity.status(HttpStatus.OK).cacheControl(CacheControl.noCache()).build();
    }

    public String[] authorizeUser(HttpHeaders headers) {
        String authenticationToken = (headers.getFirst("authorization") != null) ? headers.getFirst("authorization").split(" ")[1] : "";
        byte[] decodeToken = Base64.getDecoder().decode(authenticationToken);
        String credentialString = new String(decodeToken, StandardCharsets.UTF_8);
        String[] credentials = credentialString.split(":");
        return credentials;
    }
    
}
