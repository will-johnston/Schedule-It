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
    public Tracker() {
        users = new HashMap<>(10);
        groups = new HashMap<>(10);
        userCount = 0;
        groupCount = 0;
        timeout = 60 * 30;      //30 Minutes
    }
    public Boolean isLoggedIn(int cookie) {
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
    public int login(User user) {
        Tuple exists = isInUsers(user);
        if (exists != null) {
            return (int)exists.Item2;
        }
        int cookie;
        do {
            cookie = new BigInteger(256, new Random()).intValue();
        }
        while (users.containsKey(cookie));
        user.checkin();
        users.put(cookie, user);
        userCount++;
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
    public User getUser(int cookie) {
        if (!users.containsKey(cookie)) {
            return null;
        }
        User user = users.get(cookie);
        user.checkin();
        return user;
    }
    public User getUserByName(String username) {
        for (User user : users.values()) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }
    public boolean updateUser(int cookie, User newUser) {
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
    public boolean groupExists(int groupid) {
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
    public boolean groupExists(String groupname) {
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
    public boolean addGroup(Group group) {
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
