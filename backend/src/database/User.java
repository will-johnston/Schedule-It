package database;

import java.sql.SQLSyntaxErrorException;
import java.lang.reflect.Array;

import management.SCalendar;
import management.Tracker;
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
    ArrayList<Notification> notifications;
    ArrayList<Group> groups;
    SCalendar calendar;
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
		this.notifications = new ArrayList<>();
        this.groups = new ArrayList<>();
        this.calendar = new SCalendar(false);
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
    public static User fromDatabase(int id) {
        String[] fromDb = GetFromDb.getUserFromId(id);
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
		System.out.println("checking if the user in the database");
        try {
	    boolean exists = GetFromDb.usernameExists(username);
            if (exists) {
		System.out.println("User exists in database"); 
            }
	    else {
		System.out.println("Not a valid user");
                return false;
	    }
        }
        catch (Exception e) {
            System.out.println("AddFriendsInDb.addFriend failed to add friend");
            return false;
        }
        if (AddOrRemoveFriendsInDb.addOrRemoveFriend(username, this.username, true)) {
            friends.add(username);
	    System.out.println("AddfriendsInDb returned true");
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
    public Notification[] getNotifications() {
        try {
            System.out.println(String.format("Current notifications length: %d", this.notifications.size()));
            Notification[] dbnotifs = NotificationInDb.get(id);
            if (notifications == null || dbnotifs == null) { return null; }
            for (Notification notification : dbnotifs) {
                if (notification == null) {
                    continue;
                }
                boolean alreadyexists = false;
                for (Notification actual : this.notifications) {
                    if (actual == null) {
                        System.out.println("Actual is null");
                        continue;
                    }
                    if (actual.notifid == notification.notifid) {
                        alreadyexists = true;
                        break;
                    }
                    System.out.println(String.format("Actual: %d notification: %d", actual.notifid, notification.notifid));
                }
                if (!alreadyexists) {
                    System.out.println(String.format("Adding %d to notification list", notification.notifid));
                    this.notifications.add(notification);
                }
            }
            Notification[] copy = new Notification[this.notifications.size()];
            this.notifications.toArray(copy);
            return copy;
        }
        catch (Exception e) {
            return null;
        }
    }
    public Notification getNotificationById(int id) {
        for (int i = 0; i < 2; i++) {
            for (Notification notification : notifications) {
                if (notification.getNotifid() == id) {
                    return notification;
                }
            }
            if (i == 0) {
                //update notifications and check if in there
                updateNotifications();
            }
        }
        return null;
    }
    private void updateNotifications() {
        Notification[] dbnotifs = NotificationInDb.get(id);
        for (Notification dbnotif : dbnotifs) {
            boolean exists = false;
            for (Notification notification : notifications) {
                if (notification.getNotifid() == dbnotif.getNotifid()) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                this.notifications.add(dbnotif);
            }
        }
    }
    public boolean clearNotification(Notification notification) {
        if (notification == null) {
            System.out.println("Tried to clear null notification");
            return false;
        }
        if (NotificationInDb.remove(notification.getNotifid(), this.id)) {
            try {
                this.notifications.remove(notification);
                return true;
            }
            catch (Exception e) {
                System.out.println("Couldn't remove from User, but removed in database");
                return false;
            }
        }
        else {
            //Fail silently
            return false;
        }
    }
    public boolean addNotificatin(Notification notification) {
        if (notification == null) {
            System.out.println("Tried to clear null notification");
            return false;
        }
        if (this.notifications.contains(notification)) {
            System.out.println("Already contains notification");
            return false;
        }
        //check for matching id
        for (Notification original : notifications) {
            if (original.getNotifid() == notification.getNotifid()) {
                System.out.println("Already contains notification");
                return false;
            }
        }
        this.notifications.add(notification);
        return true;
    }
    public boolean addToGroup(Group group) {
        //check if the user is actually in the group
        if (groups.contains(group)) {
            return false;
        }
        else {
            groups.add(group);
            return true;
        }
    }
    private boolean removeWithGroupId(int groupid, Tracker tracker) {
        for (Group group : groups) {
            if (group.id == groupid) {
                if (group.removeUser(this, tracker)) {
                    groups.remove(group);
                    return true;
                }
                else {
                    return false;
                }
            }
        }
        return true;
    }
    public boolean removeFromGroup(int groupid, Tracker tracker) {
        if (!inGroup(groupid, tracker)) {
            return false;
        }
        else {
            return removeWithGroupId(groupid, tracker);
        }
    }
    //TODO get all groups of user in db
    public boolean inGroup(int groupid, Tracker tracker) {
        if (groupid == 0) {
            return false;
        }
        //check if in group in database
        for (Group group : groups) {
            if (groupid == group.id) {
                return true;
            }
        }
        ArrayList<String> members = GetFromDb.getGroupMembers(groupid);
        for (String member : members) {
            if (member.equals(this.username)) {
                Group group = Group.fromDatabase(tracker, groupid);
                if (group != null) {
                    groups.add(group);
                }
                return true;
            }
        }
        return false;
    }
	private boolean groupContainsId(int groupid) {
        if (id == 0) {
            return false;
        }
		for (Group group : groups) {
			if (group.getId() == groupid) {
				return true;
			}
		}
		return false;
	}
	public Group getGroupByName(String groupname) {
        if (groupname == null) {
            return null;
        }
        for (Group group : groups) {
            if (group.getName().equals(groupname)) {
                return group;
            }
        }
        return null;
    }
    public Group getGroupById(int id, Tracker tracker) {
        Group group = getGroupById(id);
        if (group == null) {
            group = Group.fromDatabase(tracker, id);
        }
        return group;
    }
    public Group getGroupById(int id) {
        if (id == 0) {
            return null;
        }
        for (Group group : groups) {
            if (group.getId() == id) {
                return group;
            }
        }
        return null;
    }
    public void refreshGroups(Tracker tracker) {
        ArrayList<Group> groups = GetFromDb.getGroups(this.id, tracker);
		groups.get(0).printDebug();
		//HERE IS THE ISSUE
        for (Group group : groups) {
            if (!groupContainsId(group.getId())) {
				System.out.println("adding group: " + group.getId());
                this.groups.add(group);
				System.out.println("Added group");
            }
			else {
				System.out.println("Already contains group");
			}
        }
    }
    public Group[] getGroups(Tracker tracker) {
        refreshGroups(tracker);
        Group[] arr = new Group[this.groups.size()];
        this.groups.toArray(arr);
        return arr;
    }
    public Event[] getEvents(int year, int month) {
        return calendar.getEvents(id, year, month);
    }
    public boolean addEvent(Event event) {
        return calendar.add(event);
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

    public void setLastCheckedIn(long lastCheckedIn) {
        this.lastCheckedIn = lastCheckedIn;
    }
}
