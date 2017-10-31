import java.util.Objects;

/**
 * Created by williamjohnston on 10/5/17.
 */
public class Calendar {
    String owner;
    int eventcount;
    int year;
    Event[] events;

    public Calendar(String owner, int eventcount, int year, Event[] events) {
        this.owner = owner;
        this.eventcount = eventcount;
        this.year = year;
        this.events = events;
    }

    public void setEventcount(int eventcount) {
        this.eventcount = eventcount;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setEvents(Event[] events) {
        this.events = events;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getEventcount() {
        return eventcount;
    }

    public int getYear() {
        return year;
    }

    public Event[] getEvents() {
        return events;
    }

    public String getOwner() {
        return owner;
    }
}
