package database;

import java.util.*;
import management.Tracker;
public class Group {
    int id;
    String name;
    User owner;       //username
    String imagePath;
    ArrayList<String> users;
    ArrayList<String> admins;             //not implemented
    public Group(int id, User owner, String name, String imagePath) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.imagePath = imagePath;
        this.users = new ArrayList<>(1);
        users.add(owner.username);
    }
    public static Group fromDatabase(Tracker tracker, int groupid) {
        Object[] result = GetFromDb.getGroupInfo(groupid);      //returns id, name, creator, imagePath
        if (result == null) {
            return null;
        }
        User owner = tracker.getUserByName((String)result[2]);
        if (owner == null) {
            return null;
        }
        Group group = new Group((int)result[0], owner, (String)result[1], (String)result[3]);
        ArrayList<String> members = GetFromDb.getGroupMembers(groupid);
        for (String member : members) {
            if (!group.users.contains(member)) {
                group.users.add(member);
            }
        }
        tracker.addGroup(group);
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

    public int getId() {
        return id;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getName() {
        return name;
    }
}
