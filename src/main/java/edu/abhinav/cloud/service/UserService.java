package edu.abhinav.cloud.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.abhinav.cloud.pojo.User;
import edu.abhinav.cloud.config.SecurityConfig;
import edu.abhinav.cloud.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SecurityConfig securityConfig;

    //get all users
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    //search user by username and retrieve row
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    //save and update users, encode password
    public User addUsers(User newUser) {
        newUser.setPassword(securityConfig.encodePassword(newUser.getPassword()));
        return userRepository.save(newUser);
    }

    //check user credentials and authenticate user
    public boolean authenticateUser(String username, String password) {
        User user = userRepository.findByUsername(username);
        if(user == null) return false; 
        String dbPassword = user.getPassword();
        return securityConfig.authenticatePassword(password, dbPassword);
    }
    
}