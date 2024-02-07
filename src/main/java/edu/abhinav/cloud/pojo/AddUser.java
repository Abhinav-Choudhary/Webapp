package edu.abhinav.cloud.pojo;

//*This Pojo is only used to read json string provided in request payload*
//*As Jackson mapper ignores password property, it ignores both read and write functions*
//*Use this pojo so that Jackson mapper reads password from request body 
public class AddUser {

    //Properties
    //These properties will not be stored in database or converted to Bean
    private String id;
    private String username;
    private String first_name;
    private String last_name;
    private String password;
    
    //Getter and Setters
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getFirst_name() {
        return first_name;
    }
    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }
    public String getLast_name() {
        return last_name;
    }
    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
