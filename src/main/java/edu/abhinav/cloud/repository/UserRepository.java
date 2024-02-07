package edu.abhinav.cloud.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.abhinav.cloud.pojo.User;

//Interface to implement User methods
public interface UserRepository extends JpaRepository<User, String> {

    //find user by username
    User findByUsername(String username);
}