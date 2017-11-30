package database;

import java.time.LocalDateTime;
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
    //checks for events happening today
    public void checkForEvents(Tracker tracker) {
        LocalDateTime date = LocalDateTime.now();
        Event[] events = calendar.getEvents(this.id, date.getYear(), date.getMonthValue());
        if (events == null || events.length == 0) {
            return;
        }
        for (Event event : events) {
            try {
                Notification notification = new Notification(-1, -1, "remind.event", String.format("%d,%d", this.id, event.getEventID()), null);
                notifyMembers(notification, tracker);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    //TODO
    public void updateUsers(Tracker tracker) {
        /*if (gotUsers) {
            return;
        }*/
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
            /*if (tracker.addUser(user)) {
                users.add(user.username);
            }*/
            if (!containsUsername(user.username)) {
                if (!tracker.containsUser(user.username)) {
                    tracker.addUser(user);
                }
                users.add(user.username);
            }
        }
        gotUsers = true;
    }
    public boolean containsUsername(String username) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).equals(username)) {
                return true;
            }
        }
        return false;
    }
    public String[] getMembers() {
        updateUsers(Tracker.mainTracker);
        String[] members = new String[users.size()];
        users.toArray(members);
        return members;
    }
    public void notifyMembers(Notification notification, Tracker tracker) throws Exception {
        updateUsers(tracker);
        if (notification == null || tracker == null) {
            throw new Exception("Invalid arguments");
        }
        HashMap<String, Boolean> hasBeenNotified = new HashMap<>(users.size() - 2);
        for (String username : users) {
            /*if (hasBeenNotified.containsKey(username)) {
                continue;
            }*/
            User user = tracker.getUserByName(username);
            System.out.println("Username: " + username);
            if (user.getId() == tracker.getClarence().getId()) {
                System.out.println("Skipping because of clarence");
                continue;
            }
            if (!user.isMuted(this.id)) {
                System.out.println("Adding notification to " + user.getId());
                notification.userid = user.getId();
                Notification newer = NotificationInDb.add(notification);
                if (newer != null) {
                    user.addNotification(notification);
                    //hasBeenNotified.put(username, true);
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
        Event event = calendar.getEvent(id, this.id);
        if (event == null) {
            System.out.println("Event doesn't exist in group, trying db. ID: " + id);
            return GetFromDb.getEvent(id);
        }
        else {
            return event;
        }
        //return calendar.getEvent(id, this.id);
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
        updateAdmins();
        System.out.println("Noadmins: " + noAdmins);
        if (isNoAdmins()) {
            return true;
        }
        for (String admin : admins) {
            System.out.println("Admin: " + admin);
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
        calendar.update(id);
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
        calendar.update(this.id);
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
    public void updateAdmins() {
        Tracker tracker = Tracker.mainTracker;
        ArrayList<Integer> adminsdb = GetGroupAdmins.getGroupAdmins(this.getId());
        if (adminsdb == null) {
            return;
        }
        for (int admin : adminsdb) {
            boolean contains = false;
            User user = tracker.getUserById(admin);
            if (user == null) {
                continue;
            }
            for (String username : this.admins) {
                if (user.getUsername().equals(username)) {
                    contains = true;
                    break;
                }
            }
            if (!contains) {
                this.admins.add(user.getUsername());
            }
        }
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
            System.out.println("Event doesn't exist with id: " + event.getEventID());
            return false;
        }
        //Event realEvent = getEvent(event.getEventID());
        return event.addAccept(user.getId());
    }
    public synchronized boolean addMaybe(User user, Event event) {
        if (user == null || event == null) {
            System.out.println("Can't add maybe, params are null");
            return false;
        }
        if (!eventExists(event.getEventID())) {
            System.out.println("Event doesn't exist with id: " + event.getEventID());
            return false;
        }
        //Event realEvent = getEvent(event.getEventID());
        return event.addMaybe(user.getId());
    }
    public synchronized boolean addNotGoing(User user, Event event) {
        if (user == null || event == null) {
            System.out.println("Can't add not going, params are null");
            return false;
        }
        if (!eventExists(event.getEventID())) {
            System.out.println("Event doesn't exist with id: " + event.getEventID());
            return false;
        }
        //Event realEvent = getEvent(event.getEventID());
        return event.addDecline(user.getId());
    }
    public boolean isMeGroup() {
        if (name.equals("me") || name.equals("Me")) {
            return true;
        }
        else {
            return false;
        }
    }
}
