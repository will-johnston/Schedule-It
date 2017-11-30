package database;
import com.sun.nio.sctp.NotificationHandler;
import server.SSocket;

import java.util.*;
import java.sql.Timestamp;
public class Notification implements Comparable {
    int notifid;     //notif_id
    int userid; //user_id that has the notification
    //User user;  //resolve this in the constructor
    NotificationType type;    //temporary
    String params;  //CSV
    Timestamp created;
    public Notification(int notifid, int userid, String notiftype, String params, Timestamp timestamp) throws Exception {
        this.notifid = notifid;
        this.userid = userid;
        this.params = params;
        this.created = timestamp;
        //Resolve NotificationType and User
        this.type = new NotificationType(notiftype);
    }
    public static boolean send(Notification notification) {
        //puts notification in user's list of notifications
        /*try {
            User user = User.fromDatabase(notification.userid);
            if (user == null) {
                System.out.println("Couldn't get User to send notification to");
                return false;
            }

        }
        catch (Exception e) {
            return false;
        }*/
        return false;
    }

    //if notifications are earily similar, return true
    public boolean same(Notification other) {
        if (other == null) {
            return false;
        }
        if (other.getNotifid() == notifid) {
            return true;
        }
        if (other.getType().equals(type)) {
            if (other.getUserid() == userid) {
                if (other.getParams() == params) {
                    if (created == created) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    //Getters
    public int getNotifid() {
        return this.notifid;
    }
    public int getUserid() {
        return this.userid;
    }

    public NotificationType getType() {
        return type;
    }

    public String getParams() {
        return params;
    }

    public Timestamp getCreated() {
        return created;
    }
    @Override
    public int compareTo(Object o) {
        //== 0
        //< -1
        //> 1
        if (o instanceof Notification) {
            Notification other = (Notification)o;
            if (this.notifid > other.notifid) {
                return 1;
            }
            else if (this.notifid < other.notifid) {
                return -1;
            }
            else {
                return 0;
            }
        }
        else {
            return 1;
        }
    }
}
