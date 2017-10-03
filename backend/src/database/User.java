package database;

import java.util.*;

//represents a user in the Database
public class User {
    String name;
    String username;
    String email;
    String password;
    String phone;       //can be null
    ArrayList<String> friends;          //List of usernames that this user is friends with
    int id;
    long lastCheckedIn = -1;
    public User(String name, String email, String password, String phone, int id, String username) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.id = id;
        this.username = username;
        this.friends = new ArrayList<>();
    }
    //Get the user from the database
    public static User fromDatabase(String name) {
        //return {id, username, password, name, email, phone}
        String[] fromDb = GetFromDb.getUserFromName(name);
        if (fromDb == null) {
            return null;
        }
        return new User(fromDb[1], fromDb[4], fromDb[2], fromDb[5], Integer.parseInt(fromDb[0]), fromDb[1]);
    }
    public void checkin() {
        lastCheckedIn = Calendar.getInstance(TimeZone.getTimeZone("EST")).getTimeInMillis() / 1000;
    }
    //We assume that the database has already been checked to see if the user exists
    public boolean addFriend(String username) {
        if (friends.contains(username)) {
            System.out.println("The Users are already friends");
            return false;
        }
        if (AddFriendsInDb.addFriend(username, this.username)) {
            friends.add(username);
            return true;
        }
        else {
            System.out.println("AddFriendsInDb.addFriend failed to add friend");
            return false;
        }
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

    public String getUsername() {
        return username;
    }

    public int getId() {
        return id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
