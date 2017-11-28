package database;

import java.sql.*;
import java.util.Arrays;
import java.time.LocalDateTime;

public class Event {
    int eventID = 0;
    String address;
    int groupID = 0;
    String event_name;
    String image_path;  //image_path
    String type;
    Timestamp time;     //Actually a datetime in the database, but java.sql represents both with the same object
    Timestamp created;
    String description;
    boolean is_open_ended;  //determines whether an event is open-ended or set ('is_polling_users' field in database)
    //CSVs of users that have responded
    int[] accept;   //String in database
    int acceptCount = 0;
    int[] decline;  //String in database
    int declineCount = 0;
    int[] maybe;    //String in database
    int maybeCount = 0;

    public Event(int eventID, String name, String type, String description, String image, boolean is_open_ended) {
        //fill in the basics for now
        this.eventID = eventID;
        this.event_name = name;
        this.type = type;
        this.description = description;
        this.image_path = image;
        this.is_open_ended = is_open_ended;
    }
    public static Event fromDatabase(int id) {
        if (id <= 0) {
            return null;
        }
        try {
            Event event = GetFromDb.getEvent(id);
            if (event == null) {
                System.out.println("GetFromDb returned null for event get");
                return null;
            }
            return event;
        }
        catch (Exception e) {
            System.out.println("Couldn't get from database");
            e.printStackTrace();
            return null;
        }
    }

    public synchronized void setAccept(String accept) {
        if (accept == null) return;
        if (accept.contains(",")) {
            //split csv and add
            this.accept = splitCsv(accept);
            acceptCount = this.accept.length;
        }
        else {
            int value = makeInteger(accept);
            if (value == -1) {
                this.accept = null;
            }
            else {
                this.accept = new int[]{value};
                this.acceptCount = 1;
            }
        }
    }
    public synchronized boolean addAccept(int userid) {
        if (arrExists(accept, userid)) {
            System.out.println("User has already accepted the invitation, handling silently");
            return true;
        }
        try {
            accept = resize(accept);
            accept[acceptCount] = userid;
            acceptCount++;
            return EventPutter.updateAttendanceList(1, getAcceptString(), this.eventID);
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public void setIs_open_ended(boolean is_open_ended) {
        this.is_open_ended = is_open_ended;
    }

    public synchronized void setDecline(String decline) {
        if (decline == null) return;
        if (decline.contains(",")) {
            this.decline = splitCsv(decline);
            this.declineCount = this.decline.length;
        }
        else {
            int value = makeInteger(decline);
            if (value == -1) {
                this.decline = null;
            }
            else {
                this.decline = new int[]{value};
                this.declineCount = 1;
            }
        }
    }
    public synchronized boolean addDecline(int userid) {
        if (arrExists(decline, userid)) {
            System.out.println("User has already accepted the invitation, handling silently");
            return true;
        }
        try {
            decline = resize(decline);
            decline[declineCount] = userid;
            declineCount++;
            return EventPutter.updateAttendanceList(-1, getAcceptString(), this.eventID);
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public void setMaybe(String maybe) {
        if (maybe == null) return;
        if (maybe.contains(",")) {
            this.maybe = splitCsv(maybe);
            this.maybeCount = this.maybe.length;
        }
        else {
            int value = makeInteger(maybe);
            if (value == -1) {
                this.maybe = null;
            }
            else {
                this.maybe = new int[]{value};
                this.maybeCount = 1;
            }
        }
    }
    public synchronized boolean addMaybe(int userid) {
        if (arrExists(maybe, userid)) {
            System.out.println("User has already accepted the invitation, handling silently");
            return true;
        }
        try {
            maybe = resize(maybe);
            maybe[maybeCount] = userid;
            maybeCount++;
            return EventPutter.updateAttendanceList(0, getAcceptString(), this.eventID);
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    private synchronized boolean arrExists(int[] arr, int key) {
        if (arr == null) {
            return false;
        }
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == key) {
                return true;
            }
        }
        return false;
    }
    private synchronized int[] resize(int[] arr) {
        if (arr == null) {
            return new int[1];
        }
        return Arrays.copyOf(arr, arr.length + 1);
    }
    private int[] splitCsv(String tosplit) {
        try {
            String[] splat = tosplit.split("[,]");
            int[] arr = new int[splat.length];
            for (int i = 0; i < splat.length; i++) {
                try {
                    int value = Integer.parseInt(splat[i]);
                    arr[i] = value;
                }
                catch (Exception e) {
                    arr[i] = -1;
                }
            }
            return arr;
        }
        catch (Exception etwo) {
            return null;
        }
    }
    private int makeInteger(String toconvert) {
        try {
            return Integer.parseInt(toconvert);
        }
        catch (Exception e) {
            return -1;
        }
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }


    public Timestamp getTime() {
        return time;
    }

    public int getEventID() {
        return eventID;
    }

    public String getAddress() {
        return address;
    }

    public String getDescription() {
        return description;
    }
    public int getGroupID() {
        return groupID;
    }

    public int getResponseCount() {
        return acceptCount + maybeCount + declineCount;
    }

    public int getAcceptCount() {
        return acceptCount;
    }

    public int getDeclineCount() {
        return declineCount;
    }

    public int getMaybeCount() {
        return maybeCount;
    }

    public boolean getIs_open_ended() {
        return this.is_open_ended;
    }

    public Timestamp getCreated() {
        return created;
    }

    public String getEvent_name() {
        return event_name;
    }

    public String getImage_path() {
        return image_path;
    }

    public String getType() {
        return type;
    }

    public void setName(String event_name) {
        this.event_name = event_name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public synchronized int[] getAccept() {
        return accept;
    }
    public String getAcceptString() {
        if (accept == null || accept.length == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < accept.length; i++) {
            builder.append(accept[i]);
            if (i != (accept.length - 1)) {
                builder.append(',');
            }
        }
        return builder.toString();
    }
    public synchronized int[] getDecline() {
        return decline;
    }
    public String getDeclineString() {
        if (decline == null || decline.length == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < decline.length; i++) {
            builder.append(decline[i]);
            if (i != (decline.length - 1)) {
                builder.append(',');
            }
        }
        return builder.toString();
    }

    public synchronized int[] getMaybe() {
        return maybe;
    }
    public String getMaybeString() {
        if (maybe == null || maybe.length == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < maybe.length; i++) {
            builder.append(maybe[i]);
            if (i != (maybe.length - 1)) {
                builder.append(',');
            }
        }
        return builder.toString();
    }
    public LocalDateTime getDate() {
        return time.toLocalDateTime();
    }
}
