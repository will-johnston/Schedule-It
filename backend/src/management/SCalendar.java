package management;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.*;

import database.EventPutter;
import database.GetFromDb;
import database.Event;
/*
* ScheduleIt Calendar, not java.util.calendar
* */
public class SCalendar {
    boolean isGroupCalendar;
    //use a hash table
    HashMap<Integer, YearCalendar> calendar;
    public SCalendar(boolean isGroupCalendar) {
        this.isGroupCalendar = isGroupCalendar;
        calendar = new HashMap<>(2);
    }
    public static SCalendar fromUserDatabase(int id) {
        //get events, resolve events
        if (id <= 0) {
            System.out.println("Tried to get calendar for invalid id");
            return null;
        }
        SCalendar cal = new SCalendar(false);
        Integer[] ids = GetFromDb.getEventIds(id, false);
        for (Integer integer : ids) {
            Event event = GetFromDb.getEvent(integer.intValue());
            if (event == null) {
                System.out.println("Couldn't get event with id: " + integer.intValue());
                continue;
            }
            if (!cal.addLocal(event)) {
                System.out.println("Failed to add to calendar, id: " + event.getEventID());
            }
        }
        return cal;
    }
    public static SCalendar fromGroupDatabase(int id) {
        return null;
    }
    //doesn't resolve descrepancies in the db
    //returns false if can't add
    public boolean addLocal(Event event) {
        if (event == null) {
            System.out.println("Can't addLocal event to year, event is null");
            return false;
        }
        int year = resolveYear(event.getTime());
        if (year == -1) {
            return false;
        }
        if (calendar.containsKey(year)) {
            YearCalendar yearCalendar = calendar.get(year);
            if (yearCalendar.add(event)) {
                calendar.put(year, yearCalendar);
                return true;
            }
            else {
                return false;
            }
        }
        else {
            YearCalendar yearCalendar = new YearCalendar();
            if (yearCalendar.add(event)) {
                //add to global calendar
                calendar.put(year, yearCalendar);
                return true;
            }
            else {
                return false;
            }
        }
    }
    //adds an event locally and adds it in the database
    //returns false if failed to add event
    public boolean add(Event event) {
        if (event == null) {
            System.out.println("Can't add event to year, event is null");
            return false;
        }
        Event newevent = EventPutter.addEvent(event);
        if (newevent == null) {
            return false;
        }
        else {
            if (addLocal(event)) {
                return true;
            }
            else {
                return false;
            }
        }
    }
    private int resolveYear(Timestamp time) {
        //Accept dates no earlier than 1980
        if (time == null) {
            System.out.println("Can't resolve year, time field is null");
            return -1;
        }
        LocalDateTime date = time.toLocalDateTime();
        if (date == null) {
            System.out.println("Can't resolve year, couldn't get local datetime");
            return -1;
        }
        int year = date.getYear();
        if (year < 1980) {
            System.out.println("Can't resolve year, time is before 1980");
            return -1;
        }
        return year;
    }
    public Event[] getEvents(int userid, int year, int month) {
        if (month < 1 || month > 12) {
            System.out.println("Invalid month");
            return null;
        }
        if (year < 1980) {
            System.out.println("Invalid year");
            return null;
        }
        if (userid <= 0) {
            System.out.println("Invalid userid");
            return null;
        }
        //get year, then get month
        refreshEvents(userid);
        if (!calendar.containsKey(year)) {
            System.out.println("Doesn't contain year");
            return null;
        }
        YearCalendar yearCalendar = calendar.get(year);
        HashMap<Integer, Event> monthCalendar = yearCalendar.getMonth(month);
        if (monthCalendar == null) {
            System.out.println("Doesn't contain month");
            return null;
        }
        //populate event array
        Event[] events = new Event[monthCalendar.size()];
        int i = 0;
        for (Event event : monthCalendar.values()) {
            events[i] = event;
        }
        return events;
    }
    private void refreshEvents(int userid) {
        Integer[] ids = GetFromDb.getEventIds(userid, this.isGroupCalendar);
        for (Integer id : ids) {
            int value = id.intValue();
            boolean contains = false;
            for (YearCalendar yearCalendar : calendar.values()) {
                if (yearCalendar.containsEvent(value)) {
                    contains = true;
                    break;
                }
            }
            if (!contains) {
                Event event = Event.fromDatabase(id);
                if (event == null) {
                    System.out.println("Failed to add missing local event");
                }
                else {
                    addLocal(event);
                }
            }
        }
    }
    class YearCalendar {
        //jan, feb, mar, apri, may, june, july, august, sept, oct, nov, dec
        HashMap<Integer, Event> january;   //1
        HashMap<Integer, Event> february;  //2
        HashMap<Integer, Event> march;     //3
        HashMap<Integer, Event> april;     //4
        HashMap<Integer, Event> may;       //5
        HashMap<Integer, Event> june;      //6
        HashMap<Integer, Event> july;      //7
        HashMap<Integer, Event> august;    //8
        HashMap<Integer, Event> september; //9
        HashMap<Integer, Event> october;   //10
        HashMap<Integer, Event> november;  //11
        HashMap<Integer, Event> december;  //12

        int eventCount;
        public YearCalendar() {
            eventCount = 0;
        }
        //returns false if fails to add event
        public boolean add(Event event) {
            if (event.getTime() == null) {
                System.out.println("Can't add event, time field is null");
                return false;
            }
            int month = resolveMonth(event.getTime());
            switch (month) {
                case 1:
                    return update(january, event);
                case 2:
                    return update(february, event);
                case 3:
                    return update(march, event);
                case 4:
                    return update(april, event);
                case 5:
                    return update(may, event);
                case 6:
                    return update(june, event);
                case 7:
                    return update(july, event);
                case 8:
                    return update(august, event);
                case 9:
                    return update(september, event);
                case 10:
                    return update(october, event);
                case 11:
                    return update(november, event);
                case 12:
                    return update(december, event);
                case -1:
                default:
                    return false;
            }
        }
        //put event in list, pray that list actually points to the global list
        //Please jesus only let java not be handicapped in this one facility
        private boolean update(HashMap<Integer, Event> list, Event event) {
            if (list == null || event == null) {
                System.out.println("list or event is null");
                return false;
            }
            if (list.containsKey(event.getEventID())) {
                System.out.println("Event already exists");
                return false;
            }
            //put in list
            list.put(event.getEventID(), event);
            eventCount++;
            return true;
        }
        private int resolveMonth(Timestamp timestamp) {
            try {
                LocalDateTime date = timestamp.toLocalDateTime();
                if (date == null) {
                    return -1;
                }
                return date.getMonthValue();
            }
            catch (Exception e) {
                System.out.println("YearCalendar - Couldn't resolve month");
                e.printStackTrace();
                return -1;
            }
        }
        public boolean containsEvent(int id) {
            if (january != null && january.containsKey(id)) {
                return true;
            }
            else if (february != null && february.containsKey(id)) {
                return true;
            }
            else if (march != null && march.containsKey(id)) {
                return true;
            }
            else if (april != null && april.containsKey(id)) {
                return true;
            }
            else if (may != null && may.containsKey(id)) {
                return true;
            }
            else if (june != null && june.containsKey(id)) {
                return true;
            }
            else if (july != null && july.containsKey(id)) {
                return true;
            }
            else if (august != null && august.containsKey(id)) {
                return true;
            }
            else if (september != null && september.containsKey(id)) {
                return true;
            }
            else if (october != null && october.containsKey(id)) {
                return true;
            }
            else if (november != null && november.containsKey(id)) {
                return true;
            }
            else if (december != null && december.containsKey(id)) {
                return true;
            }
            return false;
        }
        public HashMap<Integer, Event> getMonth(int month) {
            switch (month) {
                case 1:
                    return january;
                case 2:
                    return february;
                case 3:
                    return march;
                case 4:
                    return april;
                case 5:
                    return may;
                case 6:
                    return june;
                case 7:
                    return july;
                case 8:
                    return august;
                case 9:
                    return september;
                case 10:
                    return october;
                case 11:
                    return november;
                case 12:
                    return december;
                case -1:
                default:
                    return null;
            }
        }
    }
}
