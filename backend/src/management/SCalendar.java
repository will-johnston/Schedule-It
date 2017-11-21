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
    //use a hash table
    HashMap<Integer, YearCalendar> calendar;
    boolean refreshed = false;
    public SCalendar() {
        calendar = new HashMap<>(2);
    }
    /*public static SCalendar fromUserDatabase(int id) {
        //get events, resolve events
        if (id <= 0) {
            System.out.println("Tried to get calendar for invalid id");
            return null;
        }
        SCalendar cal = new SCalendar();
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
    }*/
    public static SCalendar fromDatabase(int id) {
        //get events, resolve events
        if (id <= 0) {
            System.out.println("Tried to get calendar for invalid id");
            return null;
        }
        SCalendar cal = new SCalendar();
        Integer[] ids = GetFromDb.getEventIds(id);
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
    //doesn't resolve descrepancies in the db
    //returns false if can't add
    public boolean addLocal(Event event) {
        try {
            if (event == null) {
                System.out.println("Can't addLocal event to year, event is null");
                return false;
            }
            int year = resolveYear(event.getTime());
            if (year == -1) {
                System.out.println("Year is -1");
                return false;
            }
            if (calendar.containsKey(year)) {
                YearCalendar yearCalendar = calendar.get(year);
                if (yearCalendar == null) {
                    //initialize
                    yearCalendar = new YearCalendar();
                }
                if (yearCalendar.add(event)) {
                    calendar.put(year, yearCalendar);
                    System.out.println("Added event with year: " + year);
                    if (yearCalendar.containsEvent(event.getEventID())) {
                        System.out.println("Contains event after add");
                    }
                    else {
                        System.out.println("Doesn't contains event after add");
                    }
                    return true;
                }
                else {
                    System.out.println("Couldn't add event");
                    return false;
                }
            }
            else {
                YearCalendar yearCalendar = new YearCalendar();
                if (yearCalendar.add(event)) {
                    //add to global calendar
                    calendar.put(year, yearCalendar);
                    System.out.println("Added event with year: " + year);
                    if (yearCalendar.containsEvent(event.getEventID())) {
                        System.out.println("Contains event after add");
                    }
                    else {
                        System.out.println("Doesn't contains event after add");
                    }
                    return true;
                }
                else {
                    System.out.println("Couldn't add event with non-contained yearCalendar");
                    return false;
                }
            }
        }
        catch (Exception e) {
            System.out.println("failed to addLocal");
            e.printStackTrace();
            return false;
        }
    }
    //adds an event locally and adds it in the database
    //returns false if failed to add event
    public boolean add(Event event) {
        try {
            if (event == null) {
                System.out.println("Can't add event to year, event is null");
                return false;
            }
            Event newevent = EventPutter.addEvent(event);
            if (newevent == null) {
                return false;
            } else {
                if (addLocal(event)) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        catch (Exception e) {
            System.out.println("SCalendar.add failed");
            e.printStackTrace();
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
            System.out.println(date.toString());
            System.out.println("Can't resolve year, time is before 1980");
            return -1;
        }
        return year;
    }
    public Event[] getEvents(int id, int year, int month) {
        if (month < 1 || month > 12) {
            System.out.println("Invalid month");
            return null;
        }
        if (year < 1980) {
            System.out.println("Invalid year");
            return null;
        }
        if (id <= 0) {
            System.out.println("Invalid userid");
            return null;
        }
        //get year, then get month
        //refreshEvents(id);
        Integer[] ids = GetFromDb.getEventIds(id);
        ArrayList<Event> events = new ArrayList<Event>();
        for (Integer newid : ids) {
            int value = newid.intValue();
                Event event = Event.fromDatabase(newid);
                if (event == null) {
                    System.out.println("Failed to add missing local event");
                }
                else {
                    events.add(event);
                }
        }
	
        Event[] arr = new Event[events.size()];
        events.toArray(arr);
        return arr;

        /*if (!calendar.containsKey(year)) {
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
            i++;
        }
        return events;*/
    }
    private void refreshEvents(int id) {
        if (refreshed) {
            return;
        }
        Integer[] ids = GetFromDb.getEventIds(id);
        for (Integer newid : ids) {
            int value = newid.intValue();
            boolean contains = false;
            for (YearCalendar yearCalendar : calendar.values()) {
                if (yearCalendar.containsEvent(value)) {
                    contains = true;
                    break;
                }
            }
            if (!contains) {
                Event event = Event.fromDatabase(newid);
                if (event == null) {
                    System.out.println("Failed to add missing local event");
                }
                else {
                    addLocal(event);
                }
            }
        }
        refreshed = true;
    }
    public Event getEvent(int id) {
        for (YearCalendar years : calendar.values()) {
            if (years.containsEvent(id)) {
                return years.getEvent(id);
            }
        }
        return null;
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
            System.out.println(String.format("Adding event with id:%d year:%d month:%d",event.getEventID(), resolveYear(event.getTime()), month));
            HashMap<Integer, Event> returned;
            switch (month) {
                case 1:
                    returned =  update(this.january, event);
                    if (returned != null) {
                        this.january = returned;
                        return true;
                    }
                    else {
                        return false;
                    }
                case 2:
                    returned = update(this.february, event);
                    if (returned != null) {
                        this.february = returned;
                        return true;
                    }
                    else {
                        return false;
                    }
                case 3:
                    returned = update(this.march, event);
                    if (returned != null) {
                        this.march = returned;
                        return true;
                    }
                    else {
                        return false;
                    }
                case 4:
                    returned = update(this.april, event);
                    if (returned != null) {
                        this.april = returned;
                        return true;
                    }
                    else {
                        return false;
                    }
                case 5:
                    returned = update(this.may, event);
                    if (returned != null) {
                        this.may = returned;
                        return true;
                    }
                    else {
                        return false;
                    }
                case 6:
                    returned = update(this.june, event);
                    if (returned != null) {
                        this.june = returned;
                        return true;
                    }
                    else {
                        return false;
                    }
                case 7:
                    returned = update(this.july, event);
                    if (returned != null) {
                        this.july = returned;
                    }
                    else {
                        return false;
                    }
                case 8:
                    returned =  update(this.august, event);
                    if (returned != null) {
                        this.august = returned;
                        return true;
                    }
                    else {
                        return false;
                    }
                case 9:
                    returned =  update(this.september, event);
                    if (returned != null) {
                        this.september = returned;
                        return true;
                    }
                    else {
                        return false;
                    }
                case 10:
                    returned =  update(this.october, event);
                    if (returned != null) {
                        this.october = returned;
                        return true;
                    }
                    else {
                        return false;
                    }
                case 11:
                    returned =  update(this.november, event);
                    if (returned != null) {
                        this.november = returned;
                        return true;
                    }
                    else {
                        return false;
                    }
                case 12:
                    returned =  update(this.december, event);
                    if (returned != null) {
                        this.december = returned;
                        return true;
                    }
                    else {
                        return false;
                    }
                case -1:
                default:
                    return false;
            }
        }
        //put event in list, pray that list actually points to the global list
        //Please jesus only let java not be handicapped in this one facility
        private HashMap<Integer, Event> update(HashMap<Integer, Event> list, Event event) {
            if (list == null) {
                list = new HashMap<>(2);
            }
            if (event == null) {
                System.out.println("event is null");
                return null;
            }
            if (list.containsKey(event.getEventID())) {
                System.out.println("Event already exists");
                return null;
            }
            System.out.println(String.format("List length: %d", list.size()));
            //put in list
            list.put(event.getEventID(), event);
            eventCount++;
            System.out.println(String.format("List length: %d", list.size()));
            System.out.println("Update list with event: " + event.getEventID());
            return list;
        }
        public Event getEvent(int id) {
            if (january != null && january.containsKey(id)) {
                return january.get(id);
            }
            else if (february != null && february.containsKey(id)) {
                return february.get(id);
            }
            else if (march != null && march.containsKey(id)) {
                return march.get(id);
            }
            else if (april != null && april.containsKey(id)) {
                return april.get(id);
            }
            else if (may != null && may.containsKey(id)) {
                return march.get(id);
            }
            else if (june != null && june.containsKey(id)) {
                return june.get(id);
            }
            else if (july != null && july.containsKey(id)) {
                return july.get(id);
            }
            else if (august != null && august.containsKey(id)) {
                return august.get(id);
            }
            else if (september != null && september.containsKey(id)) {
                return september.get(id);
            }
            else if (october != null && october.containsKey(id)) {
                return october.get(id);
            }
            else if (november != null && november.containsKey(id)) {
                return november.get(id);
            }
            else if (december != null && december.containsKey(id)) {
                return december.get(id);
            }
            return null;
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
        private int resolveYear(Timestamp timestamp) {
            try {
                LocalDateTime date = timestamp.toLocalDateTime();
                if (date == null) {
                    return -1;
                }
                return date.getYear();
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
