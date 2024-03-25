# WebApp Application
This repository contains source code for the project 'webapp'. A cloud based backend web application developed for the course CSYE 6225 Network Structures and Cloud Computing offered by Northeastern University and taken in Spring 2024.

# Languages and Technologies Used
The application is built using the following technologies, frameworks, languages, SKD, applications and runtimes:
- Java JDK version 17
- Spring Boot version 3.2.2
- MySQL version 8.0.33
- Hibernate ORM version 6.4.0
- Maven
- Postman (to build and use Api's)
- Google Cloud Platform
- Google Cloud SQL
- Log4j (for structured logs)

# How to run the application
The steps to run the application are as follows:
- Make sure you have Java and MySQL installed and setup with the above mentioned version
- To start the application, on the command line navigate to the folder where project is located and:
  - Run command "mvn spring:boot run"
- If you wish to build the application and generate a jar file:
  - run the command "mvn clean package"
  - the above command builds the packages and generates a jar file, cleans and removes any old artifacts, and runs the test cases
- If you have generated a jar file and wish to run the jar file
  - run the command "java -jar <PATH_TO_JAVA_JAR_FILE/JAR_FILE_NAME>" in the command line terminal.
  - in case, the command line terminal is opened in the correct path, you can omit the path to java jar file.
- If you wish to run just the test cases:
  - run the command "mvn test"

# Packer
- This project builds a machine image using packer. The build is done by "packer-build" workflow which runs after successful merge to main. The workflow is build using IAM user credentials.
- If you wish to run and test packer commands locally, below are the commands to perform various operations:
  - Packer init <FILE_NAME>
    - To scan the packer code and initialise packer
  - Packer validate <FILE_NAME>
    - To check for validation errors in packer code
  - Packer fmt <FILE_NAME>
    - To format packer files
  - Packer build <FILE_NAME>
    - To build the machine image

# API Endpoints
The following api end points are supported:
(While running locally, use "http://localhost:8080" with below mentioned end points)
- GET: /v1/user/self
  - This is an authenticated endpoint and requires an username and password
  - Returns details of current user in response
- PUT: /v1/user/self
  - This is an authenticated endpoint and requires an username and password
  - Updates first_name, last_name, password of the current user
- POST: /v1/user
  - This is not an authenticated endpoint and does not require basic authentication details
  - Creates a user with details like first_name, last_name, username, password
  - All the above mentioned fields are required
- GET: /healthz
  - This is not an authenticated endpoint and does not require basic authentication details
  - Checks and returns status 200 OK, if connection to database is healthy
- POST: /healthz
  - This is not an authenticated endpoint and does not require basic authentication details
  - This method is not allowed
- PUT: /healthz
  - This is not an authenticated endpoint and does not require basic authentication details
  - This method is not allowed
- DELETE: /healthz
  - This is not an authenticated endpoint and does not require basic authentication details
  - This method is not allowed

# Sample request payloads for different endpoints
Below are sample payloads for various endpoints
- POST: /v1/user
{
    "first_name": "Jane",
    "last_name": "Doe",
    "password": "skdjfhskdfjhg",
    "username": "jane.doe@example.com"
}
- PUT: /v1/user/self
{
    "first_name": "Jane",
    "last_name": "Doe",
    "password": "skdjfhskdfjhg"
}
