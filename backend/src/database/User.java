package database;

import java.util.*;

//represents a user in the Database
public class User {
    String name;
    String email;
    String password;
    String phone;       //can be null
    long lastCheckedIn = -1;
    public User(String name, String email, String password, String phone) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
    }
    public static User fromDatabase(String name) {
        return null;
    }
    public void checkin() {
        lastCheckedIn = Calendar.getInstance(TimeZone.getTimeZone("EST")).getTimeInMillis() / 1000;
    }
    public long getLastCheckedIn() {
        return lastCheckedIn;
    }
    //Getters
    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }
}
