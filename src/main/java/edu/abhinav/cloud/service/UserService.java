package edu.abhinav.cloud.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.abhinav.cloud.pojo.User;
import edu.abhinav.cloud.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public void addUsers(User newUser) {
        userRepository.save(newUser);
    }
    
}
