package database;

import management.*;
//describes all notifications: events, friend invites, group invites
public class Notification {
    public Notification(String title, String[] options) {

    }
    public enum NotificationType {
        friendrequest,
        grouprequest,
        personalevent,
        groupevent
    }
    public void handle(Tracker tracker) {

    }
}
