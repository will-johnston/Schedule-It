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
    ArrayList<String> admins;  //contains userID of all admins
    SCalendar calendar;
    boolean gotUsers = false;

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
        //add users
        ArrayList<String> members = GetFromDb.getGroupMembers(groupid);
        for (String member : members) {
            if (!group.users.contains(member)) {
                group.users.add(member);
            }
        }
        //add admins
        ArrayList<Integer> adminBuf = GetGroupAdmins.getGroupAdmins(groupid);
        group.admins = getUsernameFromIdList.getUsernames(adminBuf);
        //add group to tracker
        tracker.addGroup(group);
        group.updateUsers(tracker);
        return group;
        //set list of users and eventually admins
    }
    //TODO
    public void updateUsers(Tracker tracker) {
        if (gotUsers) {
            return;
        }
        ArrayList<String> members = GetFromDb.getGroupMembers(this.id);
        if (members == null || members.isEmpty() || members.size() == 0) {
            gotUsers = true;
            return;
        }
        for (String member : members) {
            User user = User.fromDatabase(member);
            if (user == null) {
                continue;
            }
            if (tracker.addUser(user)) {
                users.add(user.username);
            }
        }
        gotUsers = true;
    }
    public void notifyMembers(Notification notification, Tracker tracker) throws Exception {
        if (notification == null || tracker == null) {
            throw new Exception("Invalid arguments");
        }
        for (String username : users) {
            User user = tracker.getUserByName(username);
            if (!user.isMuted(this.id)) {
                System.out.println("Adding notification to " + user.getId());
                notification.userid = user.getId();
                Notification newer = NotificationInDb.add(notification);
                if (newer != null) {
                    user.addNotification(notification);
                }
                else {
                    System.out.println("Failed to add notification");
                }
            }
        }
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
    public boolean removeUser(User user) {
        return users.remove(user);
    }
    public void addUser(User user) {
        if (!users.contains(user.getUsername())) {
            users.add(user.username);
        }
    }
    public Event getEvent(int id) {
        return calendar.getEvent(id);
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
    public ArrayList<String> getAdmins() {
        return admins;
    }
}
