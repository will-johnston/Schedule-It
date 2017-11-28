package database;

import java.time.LocalDateTime;
import java.util.*;

import endpoints.GetMembers;
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
    boolean noAdmins = false;
    boolean gotUsers = false;

    public Group(int id, User owner, String name, String imagePath, int noAdmins) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.imagePath = imagePath;
        this.users = new ArrayList<>(1);
		this.admins = new ArrayList<>(1);
        users.add(owner.username);
        calendar = new SCalendar();
        if (noAdmins == 1) {
            this.noAdmins = true;
        }
        else {
            this.noAdmins = false;
        }
    }
    public static Group fromDatabase(Tracker tracker, int groupid) {
        System.out.println("Parsing from database");
        Object[] result = GetFromDb.getGroupInfo(groupid);      //{ groupid, groupname, creatorid, imagePath, noadmins};
        if (result == null) {
            return null;
        }
        int creatorid = (int)result[2];
        User owner = tracker.getUserById(creatorid);
        if (owner == null) {
            return null;
        }
        System.out.println("Creating the new group");
        Group group = new Group(groupid, owner, (String)result[1], (String)result[3], (int)result[4]);
        group.updateUsers(tracker);
        //add admins
        ArrayList<Integer> adminBuf = GetGroupAdmins.getGroupAdmins(groupid);
        group.admins = getUsernameFromIdList.getUsernames(adminBuf);
        //add group to tracker
        tracker.addGroup(group);
        //check for calerence
        if (!group.containsUsername(tracker.getClarence().getUsername())) {
            System.out.println("Adding clarence to group");
            //add clarence
            if (group.addUserInDb(tracker.getClarence().id)) {
                group.addUser(tracker.getClarence());
            }
        }
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
        System.out.println("Updating users with member count of " + members.size());
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
    public boolean containsUsername(String username) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).toLowerCase().equals(username.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    public String[] getMembers() {
        String[] members = new String[users.size()];
        users.toArray(members);
        return members;
    }
    public void notifyMembers(Notification notification, Tracker tracker) throws Exception {
        if (notification == null || tracker == null) {
            throw new Exception("Invalid arguments");
        }
        HashMap<String, Boolean> hasBeenNotified = new HashMap<>(users.size() - 2);
        for (String username : users) {
            if (hasBeenNotified.containsKey(username)) {
                continue;
            }
            User user = tracker.getUserByName(username);
            if (user.getId() == tracker.getClarence().getId()) {
                continue;
            }
            if (!user.isMuted(this.id)) {
                System.out.println("Adding notification to " + user.getId());
                notification.userid = user.getId();
                Notification newer = NotificationInDb.add(notification);
                if (newer != null) {
                    user.addNotification(notification);
                    hasBeenNotified.put(username, true);
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
    public boolean addUserInDb(int userid) {
        if (ModifyGroup.addUserToGroup(id, userid)) {
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
    public Event getEvent(int id) {
        return calendar.getEvent(id, this.id);
    }
    public boolean removeEvent(int eventid) {
        if (EventPutter.remove(eventid)) {
            return calendar.removeEvent(eventid);
        }
        else {
            return false;
        }
    }
    public boolean editEvent(Event event) {
        if (EventPutter.updateEvent(event.getEvent_name(), event.getDescription(), event.getTime().toString(), event.getType(), event.getEventID())) {
            if (calendar.removeEvent(event.getEventID()) && calendar.addLocal(event)) {
                return true;
            }
            else {
                System.out.println("Couldn't update local calendar");
                return false;
            }
        }
        else {
            System.out.println("Couldn't update database");
            return false;
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
	public synchronized boolean isAdmin(String username) {
        //refresh noAdmins
        System.out.println("Noadmins: " + noAdmins);
        if (isNoAdmins()) {
            return true;
        }
        for (String admin : admins) {
            if (admin.equals(username)) {
                return true;
            }
        }
        return false;
    }

    public synchronized boolean setNoAdmins(boolean noAdmins) {
        if (ModifyGroup.setNoAdmins(this.id, noAdmins)) {
            this.noAdmins = noAdmins;
            return true;
        }
        else {
            return false;
        }
    }
    public boolean isNoAdmins() {
        this.noAdmins = GetFromDb.noadmins(this.id);
        return noAdmins;
    }
    public boolean addEvent(Event event) {
        return calendar.add(event);
    }
    public Event[] getEvents(int year, int month) {
        Event events[] =  calendar.getEvents(this.id, year, month);
	    return events;
    }
    //returns all events
    public Event[] getEvents() {
        return calendar.getEvents(this.id);
    }
    // check for same time (hour, minute, second
    public boolean eventExists(Event e) {

        if (e == null) {
            System.out.println("EventExists arg is null");
            return false;
        }
        LocalDateTime dateTime = e.getDate();
        if (dateTime == null) {
            System.out.println("EventExists dateTime is null");
            return false;
        }
        Event[] events = getEvents(dateTime.getYear(), dateTime.getMonthValue());
        if (events == null) {
            System.out.println("EventExists events is null");
            return false;
        }
        for (Event event : events) {
            LocalDateTime compareTime = event.getDate();
            if (compareTime.getDayOfMonth() == dateTime.getDayOfMonth()) {
                //day is the same
                //getHour() is 24 Hs
                if (compareTime.getHour() == dateTime.getHour()) {
                    //same hour
                    if (compareTime.getMinute() == dateTime.getMinute()) {
                        //SAME time
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public boolean eventExists(int id) {
        return (calendar.getEvent(id, this.id) != null);
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

    public User getOwner() {
        return owner;
    }
    public synchronized boolean addGoing(User user, Event event) {
        if (user == null || event == null) {
            System.out.println("Can't add going, params are null");
            return false;
        }
        if (!eventExists(event.getEventID())) {
            System.out.println("Event doesn't exist");
            return false;
        }
        Event realEvent = getEvent(event.getEventID());
        return realEvent.addAccept(user.getId());
    }
    public synchronized boolean addMaybe(User user, Event event) {
        if (user == null || event == null) {
            System.out.println("Can't add maybe, params are null");
            return false;
        }
        if (!eventExists(event.getEventID())) {
            System.out.println("Event doesn't exist");
            return false;
        }
        Event realEvent = getEvent(event.getEventID());
        return realEvent.addMaybe(user.getId());
    }
    public synchronized boolean addNotGoing(User user, Event event) {
        if (user == null || event == null) {
            System.out.println("Can't add not going, params are null");
            return false;
        }
        if (!eventExists(event.getEventID())) {
            System.out.println("Event doesn't exist");
            return false;
        }
        Event realEvent = getEvent(event.getEventID());
        return realEvent.addDecline(user.getId());
    }
}
