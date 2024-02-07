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

    //GET Mapping------------------------------------------------------------------------------------------------------
    @GetMapping("/user/self")
    public ResponseEntity<Object> getUserDetails(@RequestParam(required = false) HashMap<String, String> param, @RequestHeader(required = false) HttpHeaders headers, @RequestBody(required = false) String userBody) {
        //if params are present or body is not present retun bad request
        if(param.size() > 0 || userBody != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).cacheControl(CacheControl.noCache()).build();
        }

        //get user credentials from header and check authentication
        String[] userCreds = getCreds(headers);

        //if user provides only username or password, or does not provides any credential, return bad request
        if(userCreds.length < 2) return ResponseEntity.status(HttpStatus.BAD_REQUEST).cacheControl(CacheControl.noCache()).build();
        
        boolean checkUserPassword = userService.authenticateUser(userCreds[0], userCreds[1]);
        if(!checkUserPassword) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).cacheControl(CacheControl.noCache()).build();
        }

        //retrieve user from db
        User user = userService.getUserByUsername(userCreds[0]);

        //use Jackson mapper to convert pojo class to json string
        try {
            ObjectMapper mapper = configureMapper();
            String jsonString = mapper.writeValueAsString(user);
            return ResponseEntity.status(HttpStatus.OK).cacheControl(CacheControl.noCache()).body(jsonString);
        } catch(JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).cacheControl(CacheControl.noCache()).build();
        }
    }

    //POST Mapping--------------------------------------------------------------------------------------------------
    @PostMapping("/user")
    public ResponseEntity<Object> createUser(@RequestParam(required = false) HashMap<String, String> param, @RequestHeader(required = false) HttpHeaders headers, @RequestBody(required = false) String userBody) {  
        //if params are present or body is not present retun bad request
        if(param.size() > 0 || userBody == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).cacheControl(CacheControl.noCache()).build();
        }

        //get user credentials from header, if present return bad request
        String[] userCreds = getCreds(headers);
        if(userCreds.length > 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).cacheControl(CacheControl.noCache()).build();
        }

        //configure Jackson mapper to read json string from request body
        try {
            ObjectMapper mapper = configureMapper();
            AddUser queryUser = mapper.readValue(userBody, AddUser.class);

            if(queryUser != null) {
                //check if any required property is missing in request body
                if(queryUser.getUsername() == null || queryUser.getPassword() == null || 
                queryUser.getFirst_name() == null || queryUser.getLast_name() == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).cacheControl(CacheControl.noCache()).build();
                }

                //check for username email validation
                if(!userValidations.validateEmail(queryUser.getUsername())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).cacheControl(CacheControl.noCache()).build();
                }

                //retrieve user from db based on username
                User searchedUser = userService.getUserByUsername(queryUser.getUsername());

                //if user is present, return bad request else create user
                if(searchedUser == null) {
                    User newUser = new User();
                    //translate AddUser pojo to User pojo, save user and return details as json string
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

    //PUT Mapping-----------------------------------------------------------------------------------------------
    @PutMapping("user/self")
    public ResponseEntity<Object> updateUser(@RequestParam(required = false) HashMap<String, String> param, @RequestHeader(required = false) HttpHeaders headers, @RequestBody(required = false) String userBody) {
        //if params are present or body is not present return bad request
        if(param.size() > 0 || userBody == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).cacheControl(CacheControl.noCache()).build();
        }

        try {
            //get user credentials and authorize user
            String[] userCreds = getCreds(headers);

            //if user provides only username or password, or does not provides any credential, return bad request
            if(userCreds.length < 2) return ResponseEntity.status(HttpStatus.BAD_REQUEST).cacheControl(CacheControl.noCache()).build();

            boolean checkUserPassword = userService.authenticateUser(userCreds[0], userCreds[1]);
            if(!checkUserPassword) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).cacheControl(CacheControl.noCache()).build();
            }

            //configure Jackson mapper and read request body json string
            ObjectMapper mapper = configureMapper();
            AddUser queryUser = mapper.readValue(userBody, AddUser.class);
            if(queryUser != null) {

                //the request body should contain atleast one of the below parameters
                if(queryUser.getPassword() == null || queryUser.getFirst_name() == null || 
                    queryUser.getLast_name() == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).cacheControl(CacheControl.noCache()).build();
                }

                //if user submits username, id return bad request
                if(queryUser.getId() != null || queryUser.getUsername() != null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).cacheControl(CacheControl.noCache()).build();
                }

                //retrieve user from database and update properties
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

    //get authorization credentials from header, decode base64 string, and return username, password seperately 
    public String[] getCreds(HttpHeaders headers) {
        @SuppressWarnings("null")
        String authenticationToken = (headers != null && headers.getFirst("authorization") != null) ? headers.getFirst("authorization").split(" ")[1] : "";
        
        byte[] decodeToken = Base64.getDecoder().decode(authenticationToken);
        String credentialString = new String(decodeToken, StandardCharsets.UTF_8);
        String[] credentials = credentialString.split(":");
        return credentials;
    }

    //configue Jackson mapper, with proper date time format
    public ObjectMapper configureMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        DateFormat format = DateFormat.getDateTimeInstance();
        mapper.setDateFormat(format);
        return mapper;
    }

    //convert AddUser object to User object
    private void translateAddUserToUser(AddUser addUser, User user) {
        user.setFirst_name(addUser.getFirst_name());
        user.setLast_name(addUser.getLast_name());
        user.setUsername(addUser.getUsername());
        user.setPassword(addUser.getPassword());
    }
}