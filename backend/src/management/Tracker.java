package management;

import database.*;

import java.math.BigInteger;
import java.util.*;

public class Tracker {
    HashMap<Integer, User> users;
    int count;
    int timeout;
    public Tracker() {
        users = new HashMap<>(10);
        count = 0;
        timeout = 60 * 30;      //30 Minutes
    }
    public Boolean isLoggedIn(int cookie) {
        if (!users.containsKey(cookie)) {
            return false;
        }
        User user = users.get(cookie);
        if (!hasElapsed(user.getLastCheckedIn())) {
            return false;
        }
        user.checkin();
        return true;
    }
    public int login(User user) {
        //check database for password
        int cookie = -1;
        do {
            cookie = new BigInteger(256, new Random()).intValue();
        }
        while (users.containsKey(cookie));
        user.checkin();
        users.put(cookie, user);
        return cookie;
    }
    //Returns true if the User has been inactive for too long
    private Boolean hasElapsed(long timestamp) {
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
    public int getTimeout() {
        return timeout;
    }
}
