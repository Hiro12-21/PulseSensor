package com.example.proj1;

public class User {
   // public String id;
    public String firstName;
    public String lastName;
    public String email;
   // public String password;

    public User() {
    }//This is an empty constructor (This is required if not error will occur)

    public User (String firstName, String lastName, String email) {
        //this.id = firstName + lastName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        //this.password = password;
    }//This is another constructor to initialize the following variables during the registration process


}
