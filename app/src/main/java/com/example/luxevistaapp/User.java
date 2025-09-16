package com.example.luxevistaapp;

public class User {
    private int id;
    private String username;
    private String email;
    private String contact;
    private String address;
    private String gender;
    private String country;
    private String password;

    public User() {
    }

    public User(int id, String username, String email, String contact, String address,
                String gender, String country, String password) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.contact = contact;
        this.address = address;
        this.gender = gender;
        this.country = country;
        this.password = password;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}