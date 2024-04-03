package edu.abhinav.cloud.service;

import com.google.api.core.ApiFuture;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class GoogleService {
    private final static String projectId = "csye6225-abhinav-dev";
    private final static String topicId = "verify_email";
    private final static String credentialsPath = "/opt/pubsub-service-account-key.json";
    
    Logger infoLogger = (Logger) LogManager.getLogger("WEBAPP_LOGGER_INFO");
    Logger debugLogger = (Logger) LogManager.getLogger("WEBAPP_LOGGER_DEBUG");
    Logger logger = (Logger) LogManager.getLogger("WEBAPP_LOGGER");

    public void publishPubSubMessage(String username) throws IOException, ExecutionException, InterruptedException {
    TopicName topicName = TopicName.of(projectId, topicId);
    Publisher publisher = null;

    try {
      // Get service account credentials
      Credentials credentials = GoogleCredentials.fromStream(new FileInputStream(credentialsPath));

      // Create publisher
      publisher = Publisher.newBuilder(topicName).setCredentialsProvider(() -> credentials).build();

      // Create message and update it to JSON
      String message = "User Created: " + username;
      JsonObject jsonMessage = new JsonObject();
      jsonMessage.addProperty("username", username);
      jsonMessage.addProperty("message", message);
      Gson gson = new Gson();
      String gsonMessage = gson.toJson(jsonMessage);
      debugLogger.debug("Google PubSub Debug: JSON message body: " + gsonMessage);

      // Create message
      ByteString data = ByteString.copyFromUtf8(gsonMessage);
      PubsubMessage pubsubMessage = PubsubMessage.newBuilder()
                                                  .setData(data)
                                                  .build();
      debugLogger.debug("Google PubSub Debug: pubsubMessage Ready: " + pubsubMessage);
      ApiFuture<String> messageIdFuture = publisher.publish(pubsubMessage);
      String messageId = messageIdFuture.get();
      infoLogger.info("Google PubSub Info: Created message with message Id: " + messageId);
    } catch (Exception e) {
      logger.error("Google PubSUB Error: " + e);
    } finally {
      if (publisher != null) {
        publisher.shutdown();
        publisher.awaitTermination(1, TimeUnit.MINUTES);
      }
    }
  }
}
