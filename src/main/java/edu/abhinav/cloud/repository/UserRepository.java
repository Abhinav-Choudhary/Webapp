package edu.abhinav.cloud.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.abhinav.cloud.pojo.User;

public interface UserRepository extends JpaRepository<User, Integer> {}