package edu.abhinav.cloud.service;

import java.time.Duration;
import java.time.Instant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.abhinav.cloud.pojo.VerifyUser;
import edu.abhinav.cloud.repository.VerifyUserRepository;

@Service
public class VerifyUserService {
    @Autowired
    private VerifyUserRepository verifyUserRepository;

    Logger logger = (Logger) LogManager.getLogger("WEBAPP_LOGGER");
    Logger infoLogger = (Logger) LogManager.getLogger("WEBAPP_LOGGER_INFO");
    Logger debugLogger = (Logger) LogManager.getLogger("WEBAPP_LOGGER_DEBUG");

    public void addUser(VerifyUser user){
        verifyUserRepository.save(user);
    }

    public VerifyUser getByName(String username){
        VerifyUser fetchedUser = verifyUserRepository.findByUsername(username);
        return fetchedUser;
    }

    public void updateStatus(String username) {
    
        VerifyUser user = getByName(username);
        // Get instant from database and current instant
        Instant userInstant = user.getEmailSent();
        Instant currentInstant = Instant.now();

        Duration duration = Duration.between(userInstant, currentInstant);

        long differenceInMinutes = Math.abs(duration.toMinutes());
        debugLogger.debug("Verify User Service Debug: differenceInMinutes = " + differenceInMinutes);
        // Difference should be less than 2 minutes
        if(differenceInMinutes <= 2) {
            user.setVerified(true);
            verifyUserRepository.save(user);
            infoLogger.info("Verify User Service Info : " + username + " has been verified successfully.");
        } else {
            logger.error("Verify User Service Error: Verification link timed out for user: " + username);
        }
    }
}
