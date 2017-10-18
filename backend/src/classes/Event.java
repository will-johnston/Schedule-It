import java.awt.*;
import java.util.Enumeration;

/**
 * Created by williamjohnston on 10/5/17.
 */
public class Event {
    //definitely required
    EventType type;
    String date;
    String time;
    String address;
    int id;


    public Event(EventType type, int id, String date, String time, String address) {

        this.type = type;
        this.id = id;
        this.date = date;
        this.time = time;
        this.address = address;
    }

    public Event() {}

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

