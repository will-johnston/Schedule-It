package database;

import java.util.*;
import management.SCalendar;
import management.Tracker;
public class Group {
    int id;
    String name;
    User owner;       //username
    String imagePath;
    ArrayList<String> users;
    ArrayList<String> admins;             //not implemented
    SCalendar calendar;

    public Group(int id, User owner, String name, String imagePath) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.imagePath = imagePath;
        this.users = new ArrayList<>(1);
		this.admins = new ArrayList<>(1);
        users.add(owner.username);
        calendar = new SCalendar();
    }
    public static Group fromDatabase(Tracker tracker, int groupid) {
        System.out.println("Parsing from database");
        Object[] result = GetFromDb.getGroupInfo(groupid);      //{ grupid, groupname, creatorid, imagePath};
        if (result == null) {
            return null;
        }
        int creatorid = (int)result[2];
        User owner = tracker.getUserById(creatorid);
        if (owner == null) {
            return null;
        }
        System.out.println("Creating the new group");
        Group group = new Group(groupid, owner, (String)result[1], (String)result[3]);
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
    public boolean removeUser(User user, Tracker tracker) {
            if (ModifyGroup.removeUserFromGroup(id, user.getId())) {
                users.remove(user);
                return true;
            }
            else {
                return false;
            }
    }
    public void addUser(User user) {
        if (!users.contains(user.getUsername())) {
            users.add(user.username);
        }
    }

    public int getId() {
        return id;
    }
	public void printDebug() {
		lprint("GROUP DEBUG INFO");
		lprint("ID: " + id);
		lprint("name: " + name);
		lprint("owner: " + owner.getName());
		lprint("users.length: " + users.size());
		lprint("admins.length: " + admins.size());
	}
	public boolean addEvent(Event event) {
        return calendar.add(event);
    }
    public Event[] getEvents(int year, int month) {
        return calendar.getEvents(this.id, year, month);
    }
	private void lprint(String message) {
		System.out.println(message);
	}

    public String getImagePath() {
        return imagePath;
    }

    public String getName() {
        return name;
    }
}
