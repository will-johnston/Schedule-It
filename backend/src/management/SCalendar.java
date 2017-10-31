package management;

import java.sql.Timestamp;
import java.time.LocalDateTime;
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
    //returns false if can't add
    public boolean add(Event event) {
        if (event == null) {
            System.out.println("Can't add event to year, event is null");
            return false;
        }
        if (addLocal(event)) {
            //add to database
            if (EventPutter.addEvent(event)) {
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
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
    class YearCalendar {
        //jan, feb, mar, apri, may, june, july, august, sept, oct, nov, dec
        ArrayList<Event> january;   //1
        ArrayList<Event> february;  //2
        ArrayList<Event> march;     //3
        ArrayList<Event> april;     //4
        ArrayList<Event> may;       //5
        ArrayList<Event> june;      //6
        ArrayList<Event> july;      //7
        ArrayList<Event> august;    //8
        ArrayList<Event> september; //9
        ArrayList<Event> october;   //10
        ArrayList<Event> november;  //11
        ArrayList<Event> december;  //12

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
        private boolean update(ArrayList<Event> list, Event event) {
            if (list == null || event == null) {
                System.out.println("list or event is null");
                return false;
            }
            for (Event inlist : list) {
                if (inlist.getEventID() == event.getEventID()) {
                    System.out.println("Already in list!");
                    return false;
                }
            }
            //put in list
            list.add(event);
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
    }
}
