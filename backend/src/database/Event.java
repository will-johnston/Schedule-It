package database;

import java.sql.*;

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
    int userid = 0;
    //CSVs of users that have responded
    int[] accept;   //String in database
    int[] decline;  //String in database
    int[] maybe;    //String in database

    public Event(int eventID, String name, String type, String description, String image) {
        //fill in the basics for now
        this.eventID = eventID;
        this.event_name = name;
        this.type = type;
        this.description = description;
        this.image_path = image;
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

    public void setAccept(String accept) {
        if (accept == null) return;
        if (accept.contains(",")) {
            //split csv and add
            this.accept = splitCsv(accept);
        }
        else {
            int value = makeInteger(accept);
            if (value == -1) {
                this.accept = null;
            }
            else {
                this.accept = new int[]{value};
            }
        }
    }
    public void setDecline(String decline) {
        if (decline == null) return;
        if (decline.contains(",")) {
            this.decline = splitCsv(decline);
        }
        else {
            int value = makeInteger(decline);
            if (value == -1) {
                this.decline = new int[]{value};
            }
            else {
                this.decline = new int[]{value};
            }
        }
    }
    public void setMaybe(String maybe) {
        if (maybe == null) return;
        if (maybe.contains(",")) {
            this.maybe = splitCsv(maybe);
        }
        else {
            int value = makeInteger(maybe);
            if (value == -1) {
                this.maybe = new int[]{value};
            }
            else {
                this.maybe = new int[]{value};
            }
        }
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

    public void setUserid(int id) {
        this.userid = id;
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

    public int getUserid() {
        return userid;
    }

    public int[] getAccept() {
        return accept;
    }
    public String getAcceptString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < accept.length; i++) {
            builder.append(accept[i]);
            if (i != (accept.length - 1)) {
                builder.append(',');
            }
        }
        return builder.toString();
    }

    public int[] getDecline() {
        return decline;
    }
    public String getDeclineString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < decline.length; i++) {
            builder.append(decline[i]);
            if (i != (decline.length - 1)) {
                builder.append(',');
            }
        }
        return builder.toString();
    }

    public int[] getMaybe() {
        return maybe;
    }
    public String getMaybeString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < maybe.length; i++) {
            builder.append(maybe[i]);
            if (i != (maybe.length - 1)) {
                builder.append(',');
            }
        }
        return builder.toString();
    }
}
