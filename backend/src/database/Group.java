package database;

import java.util.*;
import management.Tracker;
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
    //TODO Add getting list of users in group
    public static Group fromDatabase(Tracker tracker, int groupid) {
        String[] result = GetFromDb.getGroupInfo(groupid);      //returns id, name, creator
        if (result == null) {
            return null;
        }
        User owner = tracker.getUser(result[2]);
        if (owner == null) {
            return null;
        }
        Group group = new Group(Integer.parseInt(result[0]), owner, result[1]);
        return group;
        //set list of users and eventually admins
    }
    //TODO
    public void updateUsers() {

    }
    public boolean removeUser(User user) {
        if (user.inGroup(id)) {
            if (ModifyGroup.removeUserFromGroup(id, user.getId())) {
                users.remove(user);
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }
}
