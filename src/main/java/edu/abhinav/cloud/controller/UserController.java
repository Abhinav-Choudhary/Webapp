package edu.abhinav.cloud.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import edu.abhinav.cloud.pojo.AddUser;
import edu.abhinav.cloud.pojo.User;
import edu.abhinav.cloud.service.UserService;
import edu.abhinav.cloud.validations.UserValidations;

import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.util.Base64;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
@RestController
@RequestMapping("/v1")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    UserValidations userValidations;

    @GetMapping("/user/self")
    public ResponseEntity<Object> getUserDetails(@RequestParam(required = false) HashMap<String, String> param, @RequestHeader(required = false) HttpHeaders headers, @RequestBody(required = false) String userBody) {
        if(param.size() > 0 || userBody != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).cacheControl(CacheControl.noCache()).build();
        }
        String[] userCreds = getCreds(headers);
        if(userCreds.length < 2) return ResponseEntity.status(HttpStatus.BAD_REQUEST).cacheControl(CacheControl.noCache()).build();
        boolean checkUserPassword = userService.authenticateUser(userCreds[0], userCreds[1]);
        if(!checkUserPassword) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).cacheControl(CacheControl.noCache()).build();
        }
        User user = userService.getUserByUsername(userCreds[0]);
        try {
            ObjectMapper mapper = configureMapper();
            String jsonString = mapper.writeValueAsString(user);
            return ResponseEntity.status(HttpStatus.OK).cacheControl(CacheControl.noCache()).body(jsonString);
        }catch(JsonProcessingException e) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).cacheControl(CacheControl.noCache()).build();
        }
    }

    @PostMapping("/user")
    public ResponseEntity<Object> createUser(@RequestParam(required = false) HashMap<String, String> param, @RequestHeader(required = false) HttpHeaders headers, @RequestBody(required = false) String userBody) {  
        if(param.size() > 0 || userBody == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).cacheControl(CacheControl.noCache()).build();
        }
        String[] userCreds = getCreds(headers);
        if(userCreds.length > 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).cacheControl(CacheControl.noCache()).build();
        }
        try {
            ObjectMapper mapper = configureMapper();
            AddUser queryUser = mapper.readValue(userBody, AddUser.class);
            if(queryUser != null) {
                if(queryUser.getUsername() == null || queryUser.getPassword() == null || 
                queryUser.getFirst_name() == null || queryUser.getLast_name() == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).cacheControl(CacheControl.noCache()).build();
                }
                if(!userValidations.validateEmail(queryUser.getUsername())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).cacheControl(CacheControl.noCache()).build();
                }
                User searchedUser = userService.getUserByUsername(queryUser.getUsername());
                if(searchedUser == null) {
                    User newUser = new User();
                    translateAddUserToUser(queryUser, newUser);
                    User savedUser = userService.addUsers(newUser);
                    String jsonString = mapper.writeValueAsString(savedUser);
                    return ResponseEntity.status(HttpStatus.CREATED).cacheControl(CacheControl.noCache()).body(jsonString);
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).cacheControl(CacheControl.noCache()).build();
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).cacheControl(CacheControl.noCache()).build();
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).cacheControl(CacheControl.noCache()).build();
        }
    }

    @PutMapping("user/self")
    public ResponseEntity<Object> updateUser(@RequestParam(required = false) HashMap<String, String> param, @RequestHeader(required = false) HttpHeaders headers, @RequestBody(required = false) String userBody) {
        if(param.size() > 0 || userBody == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).cacheControl(CacheControl.noCache()).build();
        }
        try {
            String[] userCreds = getCreds(headers);
            if(userCreds.length < 2) return ResponseEntity.status(HttpStatus.BAD_REQUEST).cacheControl(CacheControl.noCache()).build();
            boolean checkUserPassword = userService.authenticateUser(userCreds[0], userCreds[1]);
            if(!checkUserPassword) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).cacheControl(CacheControl.noCache()).build();
            }
            ObjectMapper mapper = configureMapper();
            AddUser queryUser = mapper.readValue(userBody, AddUser.class);
            if(queryUser != null) {
                if(queryUser.getPassword() == null || queryUser.getFirst_name() == null || 
                    queryUser.getLast_name() == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).cacheControl(CacheControl.noCache()).build();
                }
                if(queryUser.getId() != null || queryUser.getUsername() != null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).cacheControl(CacheControl.noCache()).build();
                }
                User user = userService.getUserByUsername(userCreds[0]);
                user.setFirst_name(queryUser.getFirst_name());
                user.setLast_name(queryUser.getLast_name());
                user.setPassword(queryUser.getPassword());

                User updatedUser = userService.addUsers(user);
                if(updatedUser != null) {
                    return ResponseEntity.status(HttpStatus.NO_CONTENT).cacheControl(CacheControl.noCache()).build();
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).cacheControl(CacheControl.noCache()).build();
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).cacheControl(CacheControl.noCache()).build();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).cacheControl(CacheControl.noCache()).build();
    }

    public String[] getCreds(HttpHeaders headers) {
        String authenticationToken = (headers != null && headers.getFirst("authorization") != null) ? headers.getFirst("authorization").split(" ")[1] : "";
        byte[] decodeToken = Base64.getDecoder().decode(authenticationToken);
        String credentialString = new String(decodeToken, StandardCharsets.UTF_8);
        String[] credentials = credentialString.split(":");
        return credentials;
    }

    public ObjectMapper configureMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        DateFormat format = DateFormat.getDateTimeInstance();
        mapper.setDateFormat(format);
        return mapper;
    }

    private void translateAddUserToUser(AddUser addUser, User user) {
        user.setFirst_name(addUser.getFirst_name());
        user.setLast_name(addUser.getLast_name());
        user.setUsername(addUser.getUsername());
        user.setPassword(addUser.getPassword());
    }
}