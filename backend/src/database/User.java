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
    ArrayList<Integer> mutedGroups;                  //notif_pref_group
    int id;
    long lastCheckedIn = -1;
    boolean updatedGroups = false;
    boolean updatedNotifications = false;
    int notifAccess = 0;
    public User(String name, String email, String password, String phone, int id, String username, String notif_prefs, String imageUrl) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.id = id;
        this.username = username;
        this.friends = new ArrayList<>();
		this.notifications = new ArrayList<>();
        this.groups = new ArrayList<>();
        this.mutedGroups = new ArrayList<>();
        this.imageUrl = imageUrl;
        resolveMuted(notif_prefs);
    }
    private void resolveMuted(String prefs) {
        if (prefs == null || prefs.equals("")) {
            System.out.println("Prefs are empty");
            return;
        }
        if (prefs.contains("[,]")) {
            //split csv
            String[] split = prefs.split(",");
            this.mutedGroups = new ArrayList<>(split.length);
            int i = 0;
            for (String splat : split) {
                try {
                    mutedGroups.add(Integer.parseInt(splat));
                    System.out.println("Added " + splat);
                    i++;
                }
                catch (Exception e) {
                    System.out.println("Failed to add " + splat);
                }
            }
        }
        else {
            try {
                mutedGroups.add(Integer.parseInt(prefs));
                System.out.println("Added " + prefs);
            }
            catch (Exception e) {
                System.out.println("Failed to resolve muted groups");
                return;
            }
        }
    }
    public boolean muteGroup(int id) {
        if (inGroup(id)) {
            if (isMuted(id)) {
                System.out.println("id is muted");
                return false;
            }
            mutedGroups.add(id);
            String value = MutedToString();
            if (ModifyUserInDb.setNotificationPrefs(value, this.id)) {
                return true;
            }
            else {
                //ABORT
                mutedGroups.remove(id);
                return false;
            }
        }
        else {
            return false;
        }
    }
    public boolean unmuteGroup(int id) {
        if (inGroup(id)) {
            if (!isMuted(id)) {
                System.out.println("id is not muted");
                return false;
            }
            if (mutedGroups.size() == 1) {
                //If I remove on a size one array, throws an exception
                mutedGroups = new ArrayList<>();
            }
            else {
                mutedGroups.remove(id);
            }
            String value = MutedToString();
            if (ModifyUserInDb.setNotificationPrefs(value, this.id)) {
                return true;
            }
            else {
                //REVERT JESUS HOW DOES THIS HAPPEN
                mutedGroups.add(id);
                value = MutedToString();
                ModifyUserInDb.setNotificationPrefs(value, id);         //We're fucked anyway, so who cares if this succeeds
                return false;
            }
        }
        else {
            System.out.println("is not in group");
            return false;
        }
    }
    private String MutedToString() {
        if (mutedGroups.isEmpty()) {
            return "NULL";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < mutedGroups.size(); i++) {
            builder.append(mutedGroups.get(i));
            if (i != mutedGroups.size() - 1) {
                builder.append(',');
            }
        }
        return builder.toString();
    }
    //Get the user from the database
    public static User fromDatabase(String name) {
        //return {id, username, password, name, email, phone, notif_pref}
        String[] fromDb = GetFromDb.getUserFromName(name);
        if (fromDb == null) {
            return null;
        }
        return new User(fromDb[3], fromDb[4], fromDb[2], fromDb[5], Integer.parseInt(fromDb[0]), fromDb[1], fromDb[6], fromDb[7]);
    }
    public static User fromDatabase(int id) {
        String[] fromDb = GetFromDb.getUserFromId(id);
        if (fromDb == null) {
            return null;
        }
        return new User(fromDb[3], fromDb[4], fromDb[2], fromDb[5], Integer.parseInt(fromDb[0]), fromDb[1], fromDb[6], fromDb[7]);
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
        friends = getFriends();
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
        friends = getFriends();
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
        //friends = getFriends();
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
    public synchronized Notification[] getNotifications() {
        updateNotifications();
        Notification[] arr = new Notification[notifications.size()];
        notifications.toArray(arr);
        return arr;
        /*try {
            System.out.println(String.format("Current notifications length: %d", this.notifications.size()));
            Notification[] dbnotifs = NotificationInDb.get(id);
            if (notifications == null || dbnotifs == null) {
                return new Notification[0];
            }
            /*for (Notification notification : dbnotifs) {
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
            }*/
        /*    Notification[] copy = new Notification[this.notifications.size()];
            this.notifications.toArray(copy);
            return copy;
        }
        catch (Exception e) {
            return null;
        }*/
    }
    public synchronized Notification getNotificationById(int id) {
        updateNotifications();
        for (Notification notification : notifications) {
            if (notification.getNotifid() == id) {
                return notification;
            }
        }
        return null;
    }
    private void updateNotifications() {
        if (updatedNotifications) {
            //recheck for updates after 2 access
            if (notifAccess == 2) {
                notifAccess = 0;
            }
            else {
                return;
            }
        }
        Notification[] dbnotifs = NotificationInDb.get(id);
        if (dbnotifs == null) {
            return;
        }
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
        updatedNotifications = true;
        notifAccess++;
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
    public synchronized boolean addNotification(Notification notification) {
        if (notification == null) {
            System.out.println("Tried to clear null notification");
            return false;
        }
        /*if (this.notifications.contains(notification)) {
            System.out.println("Already contains notification");
            return false;
        }*/
        updateNotifications();
        if (notifications != null) {
            for (int i = 0; i < notifications.size(); i++) {
                if (notification.same(notifications.get(i))) {
                    return true;
                }
            }
        }
        //check for matching id
        /*for (Notification original : notifications) {
            if (original.getNotifid() == notification.getNotifid()) {
                System.out.println("Already contains notification");
                return false;
            }
        }*/
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
    //doesn't refresh groups
    public boolean isMuted(int groupid) {
        if (mutedGroups == null || mutedGroups.isEmpty()) {
            return false;
        }
        if (groups == null) {
            System.out.println("User has no groups, possible forgot to call updateGroups() before calling this");
            return false;
        }
        for (int i = 0; i < mutedGroups.size(); i++) {
            if (mutedGroups.get(i) == groupid) {
                return true;
            }
        }
        return false;
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
        return false;
    }
    public boolean removeFromGroup(int groupid, Tracker tracker) {
        return removeWithGroupId(groupid, tracker);
    }
    //TODO get all groups of user in db
    public boolean inGroup(int groupid, Tracker tracker) {
        if (inGroup(groupid)) {
            return true;
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
    public boolean inGroup(int groupid) {
        if (groupid == 0) {
            return false;
        }
        //check if in group in database
        for (Group group : groups) {
            if (groupid == group.id) {
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
        refreshGroups(tracker);
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
        /*if (updatedGroups) {
            return;
        }*/
        ArrayList<Group> groups = GetFromDb.getGroups(this.id, tracker);
        if (groups == null || groups.isEmpty() || groups.size() == 0) {
            //complains that size can never be zero, but it can
            //so stop complaining
            return;
        }
        System.out.println("Starting loop");
        System.out.println("Size: " + groups.size());
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
        this.updatedGroups = true;
    }
    public Group[] getGroups(Tracker tracker) {
        refreshGroups(tracker);
        Group[] arr = new Group[this.groups.size()];
        this.groups.toArray(arr);
        return arr;
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

    public ArrayList<Integer> getMutedGroups() {
        return mutedGroups;
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
