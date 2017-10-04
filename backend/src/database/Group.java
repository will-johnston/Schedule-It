package database;

import java.util.*;
public class Group {
    int id;
    String name;
    User owner;       //username
    ArrayList<User> users;
    ArrayList<User> admins;             //not implemented
    public Group(int id, User owner, String name) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.users = new ArrayList<>(1);
        users.add(owner);
    }
    //TODO
    public static Group fromDatabase() {
        return null;
    }
}
