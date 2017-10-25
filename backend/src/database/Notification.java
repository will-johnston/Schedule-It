package database;
import java.util.*;
import java.sql.Timestamp;
public class Notification {
    int id;     //notif_id
    int userid; //user_id
    User user;
    String type;    //temporary
    String params;  //CSV
    Timestamp created;
}
