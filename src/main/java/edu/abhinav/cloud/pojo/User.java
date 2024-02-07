package edu.abhinav.cloud.pojo;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity(name = "user")
@Table(name="user")
@Component
public class User {
    @Id
    @UuidGenerator
    private String id; //save id as UUID

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "first_name", nullable = false)
    private String first_name;

    @Column(name = "last_name", nullable = false)
    private String last_name;

    @Column(name = "password", nullable = false)
    @JsonIgnore //ignore password property from Jackson mapper
    private String password;

    @ReadOnlyProperty
    @Column(name = "account_created")
    @CreationTimestamp  //updates on creation
    private Instant account_created;    //Instant is used as it stores data as date time

    @ReadOnlyProperty
    @Column(name="account_updated")
    @UpdateTimestamp    //updates on updation and creation
    private Instant account_updated;    //Instant is used as it stores data as date time

    //Getter and Setters
    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public Instant getAccount_created() {
        return account_created;
    }

    public Instant getAccount_updated() {
        return account_updated;
    }
}