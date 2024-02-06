package edu.abhinav.cloud;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import edu.abhinav.cloud.pojo.User;
import edu.abhinav.cloud.service.UserService;

@SpringBootTest
class CloudApplicationTests {

	@Autowired
    private UserService userService;

	@Test
	void testAddUsers() {
		User newUser = new User();
		newUser.setUsername("abhinav.choudhary@northeastern.edu");
		newUser.setFirst_name("Abhinav");
		newUser.setLast_name("Choudhary");
		newUser.setPassword("password1234");

		userService.addUsers(newUser);	
	}
}