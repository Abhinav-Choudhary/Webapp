package edu.abhinav.cloud.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.abhinav.cloud.pojo.VerifyUser;

public interface VerifyUserRepository extends JpaRepository<VerifyUser, String> {

    VerifyUser findByUsername(String username);
    
}
