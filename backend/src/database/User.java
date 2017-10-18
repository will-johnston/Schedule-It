package database;

import java.util.*;

//represents a user in the Database
public class User {
    String name;
    String username;
    String email;
    String password;
    String phone;       //can be null
    String imageUrl;
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
    private boolean areFriendsInDb(String username) {
        String[] dbfriends = FindUsersFriends.findFriends(this.username);
        if (friends == null) {
            return false;
        }
        for (String friend : dbfriends) {
            if (!friends.contains(friend)) {
                //System.out.println("Added ");
                friends.add(friend);
            }
            //System.out.println(friend);
        }
        return friends.contains(username);
    }
    //We assume that the database has already been checked to see if the user exists
    public boolean addFriend(String username) {
        if (friends.contains(username)) {
            System.out.println("Already contained in friends list");
            System.out.println("The Users are already friends");
            return false;
        }
        else {
            System.out.println("Not in friends list");
            if (areFriendsInDb(username)) {
                System.out.println("Friends after db update, returning false");
                friends.add(username);
                return false;           //Were already friends
            }
            else {
                System.out.println("Not in friends list after update");
            }
        }
        if (AddOrRemoveFriendsInDb.addOrRemoveFriend(username, this.username, true)) {
            friends.add(username);
            return true;
        }
        else {
            System.out.println("AddFriendsInDb.addFriend failed to add friend");
            return false;
        }
    }
    //We assume that the database has already been checked to see if the user exists
    public boolean removeFriend(String username) {
        if (!friends.contains(username)) {
            if (areFriendsInDb(username)) {
                //This is silly, but it works
                friends.add(username);
                System.out.println("In friends list: " + friends.contains(username));
                //continue to remove from db
            }
            else {
                System.out.println("Are not friends");
                return false;
            }
        }
        if (AddOrRemoveFriendsInDb.addOrRemoveFriend(username, this.username, false)) {
            friends.remove(username);
            return true;
        }
        else {
            System.out.println("AddFriendsInDb.addFriend failed to remove friend");
            return false;
        }
    }
    private boolean inFriends(String name) {
        return friends.contains(name);
    }
    public ArrayList<String> getFriends() {
        //update from database
        ArrayList<String> names = GetFromDb.getFriendIds(this.id);
        for (String name : names) {
            if (!inFriends(name)) {
                friends.add(name);
            }
        }
        return friends;
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

    public String getImageUrl() { return imageUrl; }

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

    public void setImageUrl(String url) {this.imageUrl = url; }
}
