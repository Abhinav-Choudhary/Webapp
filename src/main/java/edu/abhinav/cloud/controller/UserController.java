package edu.abhinav.cloud.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import edu.abhinav.cloud.pojo.AddUser;
import edu.abhinav.cloud.pojo.User;
import edu.abhinav.cloud.pojo.VerifyUser;
import edu.abhinav.cloud.service.GoogleService;
import edu.abhinav.cloud.service.UserService;
import edu.abhinav.cloud.service.VerifyUserService;
import edu.abhinav.cloud.validations.UserValidations;

import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.util.Base64;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    @Autowired
    VerifyUserService verifyUserService;

    @Autowired
    GoogleService googleService;

    Logger logger = (Logger) LogManager.getLogger("WEBAPP_LOGGER");
    Logger infoLogger = (Logger) LogManager.getLogger("WEBAPP_LOGGER_INFO");
    Logger warnLogger = (Logger) LogManager.getLogger("WEBAPP_LOGGER_WARN");
    Logger debugLogger = (Logger) LogManager.getLogger("WEBAPP_LOGGER_DEBUG");

    //GET Mapping------------------------------------------------------------------------------------------------------
    @GetMapping("/user/self")
    public ResponseEntity<Object> getUserDetails(@RequestParam(required = false) HashMap<String, String> param, @RequestHeader(required = false) HttpHeaders headers, @RequestBody(required = false) String userBody) {
        //if params are present or body is not present retun bad request
        if(param.size() > 0 || userBody != null) {
            logger.error("User Get error: Params are present or body is not null");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).cacheControl(CacheControl.noCache()).build();
        }

        //get user credentials from header and check authentication
        String[] userCreds = getCreds(headers);

        //if user provides only username or password, or does not provides any credential, return bad request
        if(userCreds.length < 2) {
            logger.error("User Get error: Enter both username and password");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).cacheControl(CacheControl.noCache()).build();
        }

        //check if user is verified
        VerifyUser verifyUser = verifyUserService.getByName(userCreds[0]);
        if(verifyUser.isVerified() != true) {
            logger.error("User Get Error: User not verified");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).cacheControl(CacheControl.noCache()).build();
        }
        
        boolean checkUserPassword = userService.authenticateUser(userCreds[0], userCreds[1]);
        if(!checkUserPassword) {
            logger.error("User Get error: Unauthorized Access");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).cacheControl(CacheControl.noCache()).build();
        }

        //retrieve user from db
        User user = userService.getUserByUsername(userCreds[0]);
        debugLogger.debug("User Get Debug: User retrieved from database: " + user.getUsername());

        //use Jackson mapper to convert pojo class to json string
        try {
            ObjectMapper mapper = configureMapper();
            String jsonString = mapper.writeValueAsString(user);
            debugLogger.debug("User Get Debug: Json String from request body: " + jsonString);
            infoLogger.info("User Get: User Found: " + user.getUsername());
            return ResponseEntity.status(HttpStatus.OK).cacheControl(CacheControl.noCache()).contentType(MediaType.APPLICATION_JSON).body(jsonString);
        } catch(JsonProcessingException e) {
            logger.error("User Get error: " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).cacheControl(CacheControl.noCache()).build();
        }
    }

    //POST Mapping--------------------------------------------------------------------------------------------------
    @PostMapping("/user")
    public ResponseEntity<Object> createUser(@RequestParam(required = false) HashMap<String, String> param, @RequestHeader(required = false) HttpHeaders headers, @RequestBody(required = false) String userBody) {  
        //if params are present or body is not present retun bad request
        if(param.size() > 0 || userBody == null) {
            logger.error("User Post error: Params are present or body is null");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).cacheControl(CacheControl.noCache()).build();
        }

        //get user credentials from header, if present return bad request
        String[] userCreds = getCreds(headers);
        if(userCreds.length > 0) {
            logger.error("User Post error: Post does not require authentication");
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
                    logger.error("User Post Error: Username, Password, First Name, Last Name Missing");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).cacheControl(CacheControl.noCache()).build();
                }

                //if user provides Id in request body, return bad request
                if(queryUser.getId() != null) {
                    logger.error("User Post Error: Id should not be present in request body");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).cacheControl(CacheControl.noCache()).build();
                }

                //check for username email validation
                if(!userValidations.validateEmail(queryUser.getUsername())) {
                    logger.error("User Post Error: Username does not pass validation checks");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).cacheControl(CacheControl.noCache()).build();
                }

                //retrieve user from db based on username
                User searchedUser = userService.getUserByUsername(queryUser.getUsername());

                //if user is present, return bad request else create user
                if(searchedUser == null) {
                    debugLogger.debug("User Post Debug: searchedUser is null");
                    User newUser = new User();
                    //translate AddUser pojo to User pojo, save user and return details as json string
                    translateAddUserToUser(queryUser, newUser);
                    User savedUser = userService.addUsers(newUser);
                    String jsonString = mapper.writeValueAsString(savedUser);
                    debugLogger.debug("User Post Debug: savedUser Json String: " + jsonString);
                    infoLogger.info("User Post: User Successfully Created: " + savedUser.getUsername());
                    try {
                        // Add entry in verify_user table
                        VerifyUser verifyUser = new VerifyUser(savedUser.getUsername());
                        verifyUserService.addUser(verifyUser);
                        infoLogger.info("User Post: Added user to Verify User Table");
                        // Create PubSub message
                        googleService.publishPubSubMessage(savedUser.getUsername());
                        infoLogger.info("User Post: PubSub message created");
                    } catch(Exception e) {
                        logger.error("User Post Error: " + e);
                    }
                    return ResponseEntity.status(HttpStatus.CREATED).cacheControl(CacheControl.noCache()).contentType(MediaType.APPLICATION_JSON).body(jsonString);
                } else {
                    debugLogger.debug("User Post Debug: searchedUser: " + searchedUser.getUsername());
                    logger.error("User Post error: User Already Exists");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).cacheControl(CacheControl.noCache()).build();
                }
            } else {
                logger.error("User Post error: Incorrect Body");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).cacheControl(CacheControl.noCache()).build();
            }
        } catch (JsonProcessingException e) {
            logger.error("User Post error: " + e);
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).cacheControl(CacheControl.noCache()).build();
        }
    }

    //PUT Mapping-----------------------------------------------------------------------------------------------
    @PutMapping("user/self")
    public ResponseEntity<Object> updateUser(@RequestParam(required = false) HashMap<String, String> param, @RequestHeader(required = false) HttpHeaders headers, @RequestBody(required = false) String userBody) {
        //if params are present or body is not present return bad request
        if(param.size() > 0 || userBody == null) {
            logger.error("User Put error: Params are present or body is null");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).cacheControl(CacheControl.noCache()).build();
        }

        try {
            //get user credentials and authorize user
            String[] userCreds = getCreds(headers);

            //if user provides only username or password, or does not provides any credential, return bad request
            if(userCreds.length < 2) {
                logger.error("User Put error: Enter both username and password");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).cacheControl(CacheControl.noCache()).build();
            }

            //check if user is verified
            VerifyUser verifyUser = verifyUserService.getByName(userCreds[0]);
            if(verifyUser.isVerified() != true) {
                logger.error("User Put Error: User not verified");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).cacheControl(CacheControl.noCache()).build();
            }

            boolean checkUserPassword = userService.authenticateUser(userCreds[0], userCreds[1]);
            if(!checkUserPassword) {
                logger.error("User Put error: Unauthorized Access");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).cacheControl(CacheControl.noCache()).build();
            }

            //configure Jackson mapper and read request body json string
            ObjectMapper mapper = configureMapper();
            AddUser queryUser = mapper.readValue(userBody, AddUser.class);
            if(queryUser != null) {

                //the request body should contain atleast one of the below parameters
                if(queryUser.getPassword() == null) {
                    warnLogger.warn("User Put Warning: Password is not present in body");
                    if(queryUser.getFirst_name() == null) {
                        warnLogger.warn("User Put Warning: First Name is not present in body");
                        if(queryUser.getLast_name() == null) {
                            logger.error("User Put error: First Name, Last Name, Password fields are not present");
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).cacheControl(CacheControl.noCache()).build();
                        }
                    }
                }

                //if user submits username, id return bad request
                if(queryUser.getId() != null || queryUser.getUsername() != null) {
                    debugLogger.debug("User Put Debug: queryUser.getId(): " + queryUser.getId());
                    debugLogger.debug("User Put Debug: queryUser.getUsername(): " + queryUser.getUsername());
                    logger.error("User Put error: Id, Username should not be present in request body");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).cacheControl(CacheControl.noCache()).build();
                }

                //retrieve user from database and update properties
                User user = userService.getUserByUsername(userCreds[0]);
                if(queryUser.getFirst_name() != null) user.setFirst_name(queryUser.getFirst_name());
                if(queryUser.getLast_name() != null) user.setLast_name(queryUser.getLast_name());
                if(queryUser.getPassword() != null) {
                    user.setPassword(queryUser.getPassword());
                } else {
                    user.setPassword(userCreds[1]);
                }
                User updatedUser = userService.addUsers(user);

                if(updatedUser != null) {
                    infoLogger.info("User Put Info: User Successfully Updated: " + updatedUser.getUsername());
                    return ResponseEntity.status(HttpStatus.NO_CONTENT).cacheControl(CacheControl.noCache()).build();
                }
            } else {
                logger.error("User Put error: Request Body Empty");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).cacheControl(CacheControl.noCache()).build();
            }
        } catch (JsonProcessingException e) {
            logger.error("User Put error: " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).cacheControl(CacheControl.noCache()).build();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).cacheControl(CacheControl.noCache()).build();
    }

    //get authorization credentials from header, decode base64 string, and return username, password seperately 
    public String[] getCreds(HttpHeaders headers) {
        @SuppressWarnings("null")
        String authenticationToken = (headers != null && 
                                    headers.getFirst("authorization") != null) ? 
                                    headers.getFirst("authorization").split(" ")[1] : "";
        
        byte[] decodeToken = Base64.getDecoder().decode(authenticationToken);
        String credentialString = new String(decodeToken, StandardCharsets.UTF_8);
        String[] credentials = !credentialString.isEmpty() ? credentialString.split(":") : new String[0];
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