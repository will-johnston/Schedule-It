package management;

import database.*;
import endpoints.Tuple;

import java.math.BigInteger;
import java.util.*;

public class Tracker {
    volatile HashMap<Integer, User> users;           //logged in <cookie, User>
    volatile HashMap<Integer, Group> groups;
    volatile int userCount;
    volatile int groupCount;
    int timeout;
    User Clarence;
    public Tracker() {
        users = new HashMap<>(10);
		groups = new HashMap<>(10);
        userCount = 0;
        groupCount = 0;
        timeout = 60 * 30;      //30 Minutes
        Clarence = User.fromDatabase(46);
        //timeout = 30;           //testy
    }
    public synchronized Boolean isLoggedIn(int cookie) {
        if (!users.containsKey(cookie)) {
            return false;
        }
        System.out.println("User exists");
        User user = users.get(cookie);
        if (!hasElapsed(user.getLastCheckedIn())) {
            return false;
        }
        user.checkin();
        return true;
    }
    public synchronized int login(User user) {
        Tuple exists = isInUsers(user);
        if (exists != null) {
            //checkin
            user.checkin();
            updateUser((int)exists.Item2 ,user);
            return (int)exists.Item2;
        }
        //else login to the system
        int cookie = makeCookie();
        user.checkin();
        users.put(cookie, user);
        userCount++;
        return cookie;
    }
    public synchronized User getClarence() {
        return Clarence;
    }
    public synchronized boolean removeGroup(int id) {
        if (groups.remove(id) != null) {
            return true;
        }
        else {
            return false;
        }
    }
    private synchronized int makeCookie() {
        int cookie = 0;
        do {
            cookie = new BigInteger(256, new Random()).intValue();
        }
        while (users.containsKey(cookie));
        return cookie;
    }
    private Tuple isInUsers(User user) {
        for (Map.Entry value : users.entrySet()) {
            User userValue = (User)value.getValue();
            if (userValue.getUsername().equals(user.getUsername())) {
                return new Tuple(true, value.getKey());
            }
        }
        return null;
    }
    //Returns true if the User has been inactive for too long
    private boolean hasElapsed(long timestamp) {
        long currentTime = Calendar.getInstance(TimeZone.getTimeZone("EST")).getTimeInMillis() / 1000;
        System.out.println("Current time: " + currentTime);
        System.out.println("Timestamp: " + timestamp);
        System.out.println("Difference: " + (currentTime - timestamp));
        if (currentTime - timestamp > timeout) {
            return false;
        }
        if (currentTime - timestamp < 0) {
            return false;
        }
        return true;
    }
    public void setTimeout(int timeout) {
        if (timeout > 0) {
            this.timeout = timeout;
        }
    }
    public synchronized User getUser(int cookie) {
        if (!users.containsKey(cookie)) {
            return null;
        }
        User user = users.get(cookie);
        user.checkin();
        return user;
    }
    public synchronized User getUserByName(String username) {
        if (username == null) {
            return null;
        }
        for (User user : users.values()) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        User fromdb = User.fromDatabase(username);
        if (fromdb != null) {
            addUser(fromdb);
            return fromdb;
        }
        return null;
    }
    public synchronized User getUserById(int id) {
		if (id == 0) {
			return null;
		} 
        for (User user : users.values()) {
            if (user.getId() == id) {
                return user;
            }
        }
        //not in tracker, check if in db
        User user = User.fromDatabase(id);
		if (user != null) {
		    //add it to tracker
            addUser(user);
        }
        return user;    //null if couldn't find in db
    }
    public synchronized Group getGroupById(int id) {
        if (id == 0) {
            return null;
        }
        if (groups.containsKey(id)) {
            return groups.get(id);
        }
        else {
            //check if in database
            Group group = Group.fromDatabase(this, id);
            if (group == null) {
                return null;
            }
            groups.put(id, group);
            groupCount++;
            return group;
        }
    }
    public synchronized boolean addUser(User newuser) {
        //adds to tracker, but doesn't login
        //check if in tracker
        boolean intracker = false;
        for (User user : users.values()) {
            if (user.getId() == newuser.getId()) {
                intracker = true;
                break;
            }
        }
        if (intracker) {
            return false;
        }
        else {
            newuser.setLastCheckedIn(0);
            users.put(makeCookie(), newuser);
            userCount++;
            return true;
        }
    }
    public synchronized boolean updateUser(int cookie, User newUser) {
        if (!users.containsKey(cookie)) {
            return false;
        }
        try {
            newUser.checkin();
            users.put(cookie, newUser);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
    public synchronized boolean groupExists(int groupid) {
        if (!groups.containsKey(groupid)) {
            //check if it's in the database
            if (RetrieveGroupInfo.getGroupName(groupid) != null) {
                Group group = Group.fromDatabase(this, groupid);
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return true;
        }
    }
    //TODO check if group exists in db (by name)
    public synchronized boolean groupExists(String groupname) {
        if (groupname == null) {
            System.out.println("Groupname is null");
            return false;
        }
        if (groups == null || groups.values() == null) {
            System.out.println("Groups or groups.values is null");
            return false;
        }
        for (Group group : groups.values()) {
            if (group != null && group.getName().equals(groupname)) {
                return true;
            }
        }
        //check the database
        return false;
    }
    //TODO make sure users get updated as part of a group
    public synchronized boolean addGroup(Group group) {
        if (groups.containsKey(group.getId())) {
            return false;
        }
        else {
            //Another fine addition to my collection
            groups.put(group.getId(), group);
            groupCount++;
            return true;
        }
    }

    public int getTimeout() {
        return timeout;
    }
}
