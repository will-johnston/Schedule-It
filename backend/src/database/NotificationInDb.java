package database;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

import java.sql.*;
import java.util.*;

/*
Noification schema
notif_id: int
user_id: int
type: string(32)
parameters: string(500)
created: TIMESTAMP
 */
public class NotificationInDb {
    //returns true if the notification exists in the database
    public static boolean exists(int userid, String type, String parameters) {
        MysqlConnectionPoolDataSource ds = null;  //mysql schedule database
        ds = DataSourceFactory.getDataSource();
        if (ds == null) {
            System.out.println("null data source getUserFromName");
            return true;
        }
        Connection connection = null;  //used to connect to database
        Statement statement = null;  //statement to enter command
        ResultSet result = null;  //output after query
        try {
            //set up connection
            connection = ds.getConnection();
            //create statement
            //statement = connection.createStatement();
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            //query  database
            String query = String.format("SELECT * FROM notifications WHERE user_id=%d AND type='%s' AND parameters='%s';", userid, type, parameters);
            System.out.println(query);
            result = statement.executeQuery(query);
            if (result.next()) {
                return true;
            }
            else {
                return false;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            /*try {
                if(result != null) result.close();
                if(statement != null) statement.close();
                if(connection != null) connection.close();

            } catch (SQLException r) {
                r.printStackTrace();
            }*/
            return true;
        }
    }
    //Gets all notifications for a given user
    public static Notification[] get(int id) {
        MysqlConnectionPoolDataSource ds = null;  //mysql schedule database
        ds = DataSourceFactory.getDataSource();
        if (ds == null) {
            System.out.println("null data source getUserFromName");
            return null;
        }
        Connection connection = null;  //used to connect to database
        Statement statement = null;  //statement to enter command
        ResultSet result = null;  //output after query
        try {
            //set up connection
            connection = ds.getConnection();
            //create statement
            //statement = connection.createStatement();
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            //query  database
            String query = String.format("SELECT * FROM notifications WHERE user_id=%d;", id);
            System.out.println(query);
            result = statement.executeQuery(query);
            //System.out.println("Checking for null");
            /*if (result == null) {
                return null;
            }*/
            ArrayList<Notification> list = new ArrayList<>(5);
            while (result.next()) {
                int userid = result.getInt("user_id");
                int notifid = result.getInt("notif_id");
                String type = result.getString("type");
                String parameters = result.getString("parameters");
                Timestamp timestamp = result.getTimestamp("created");
                try {
                    Notification not = new Notification(notifid, userid, type, parameters, timestamp);
					list.add(not);
					System.out.println("Added: " + not.notifid);
                }
                catch (Exception adderror) {
                    adderror.printStackTrace();
                    System.out.println("Failed to add Notification with id: " + notifid);
                }
            }
            if (list.isEmpty()) {
                return null;
            }
            Notification[] notifications = new Notification[list.size()];
            list.toArray(notifications);
            return notifications;
        }
        catch (SQLException e) {
            e.printStackTrace();
            /*try {
                if(result != null) result.close();
                if(statement != null) statement.close();
                if(connection != null) connection.close();

            } catch (SQLException r) {
                r.printStackTrace();
            }*/
            return null;
        }
    }
    //notification only contains type, params, and user_id
    //Returns fully formed Notification with notif_id and created
    public static Notification add(Notification notification) {
        //check if notification already exists
        if (exists(notification.userid, notification.getType().toString(), notification.params)) {
            return null;
        }

        MysqlConnectionPoolDataSource ds = null;  //mysql schedule database
        ds = DataSourceFactory.getDataSource();
        if (ds == null) {
            System.out.println("null data source getUserFromName");
            return null;
        }
        Connection connection = null;  //used to connect to database
        Statement statement = null;  //statement to enter command
        //ResultSet result = null;  //output after query
        try {
            //set up connection
            connection = ds.getConnection();
            //create statement
            //statement = connection.createStatement();
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            //query  database
            //String query = String.format("SELECT * FROM notifications WHERE username='%s';", username);
            String query = String.format("INSERT INTO notifications VALUES(NULL, %d,'%s','%s',NULL);",
                    notification.userid, notification.type.toString(), notification.params);
            System.out.println(query);
            //result = statement.executeQuery(query);
            int response = statement.executeUpdate(query);
            if (response == 0) {
                System.out.println("Didn't update any rows");
                return null;
            }
            else {
                System.out.println("Updated " + response + " rows");
            }
            query = String.format("SELECT * FROM notifications WHERE user_id='%s' AND type='%s' AND parameters='%s';", notification.userid, notification.type.toString(), notification.params);
            ResultSet results = statement.executeQuery(query);
            if (results.next()) {
                notification.notifid = results.getInt("notif_id");
                notification.created = results.getTimestamp("created");
                return notification;
            }
            else {
                return null;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            /*try {
                if(result != null) result.close();
                if(statement != null) statement.close();
                if(connection != null) connection.close();

            } catch (SQLException r) {
                r.printStackTrace();
            }*/
            return null;
        }
    }
    public static boolean remove(int notifid, int userid) {
        //check if notification already exists
        MysqlConnectionPoolDataSource ds = null;  //mysql schedule database
        ds = DataSourceFactory.getDataSource();
        if (ds == null) {
            System.out.println("null data source getUserFromName");
            return false;
        }
        Connection connection = null;  //used to connect to database
        Statement statement = null;  //statement to enter command
        //ResultSet result = null;  //output after query
        try {
            //set up connection
            connection = ds.getConnection();
            //create statement
            //statement = connection.createStatement();
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            //query  database
            //String query = String.format("SELECT * FROM notifications WHERE username='%s';", username);
            String query = String.format("DELETE FROM notifications WHERE notif_id=%d AND user_id=%d\n", notifid, userid);
            System.out.println(query);
            //result = statement.executeQuery(query);
            int response = statement.executeUpdate(query);
            if (response == 0) {
                System.out.println("Didn't update any rows");
            }
            return true;
        }
        catch (SQLException e) {
            e.printStackTrace();
            /*try {
                if(result != null) result.close();
                if(statement != null) statement.close();
                if(connection != null) connection.close();

            } catch (SQLException r) {
                r.printStackTrace();
            }*/
            return false;
        }
    }
}
