package database;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

import java.sql.*;

public class EventPutter {
    public static Event addEvent(Event event) {
        if (event == null) {
            return null;
        }
        //gets all the event ids connected to the user
        MysqlConnectionPoolDataSource ds = null;  //mysql schedule database
        ds = DataSourceFactory.getDataSource();
        if (ds == null) {
            System.out.println("null data source getUserFromName");
            return null;
        }
        Connection connection = null;  //used to connect to database
        Statement statement = null;  //statement to enter command
        try {
            //set up connection
            connection = ds.getConnection();
            //create statement
            //statement = connection.createStatement();
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            //query  database
            //eventid, address, groupid, event_name, expiration_time, is_polling_users, image_path, type, time,
            // description, accept, decline, maybe, userid, created
            String values = String.format("%d,%s,%d,%s,%s,%d,%s,%s,%s,%s,%s,%s,%s,%d,NULL", event.getEventID(),
                    event.getAddress(), event.getGroupID(), event.getEvent_name(), "NULL", "-1", event.getImage_path(),
                    event.getType(), event.getTime(), event.getDescription(), event.getAcceptString(), event.getDeclineString(), event.getMaybeString(),
                    event.getUserid(), event.getCreated());
            String query = String.format("INSERT INTO notifications VALUES(%s);", values);
            System.out.println(query);
            int result = statement.executeUpdate(query);
            if (result == 0) {
                System.out.println("Didn't update anything");
                return null;
            }
            else {
                //get the eventID
                query = String.format("SELECT * FROM events WHERE event_name='%s' AND userid=%d AND description='%s' AND time='%s';", event.getUserid(),
                        event.getDescription(), event.getTime());
                ResultSet newevent = statement.executeQuery(query);
                if (newevent.next()) {
                    event.setUserid(newevent.getInt("eventID"));
                }
                else {
                    System.out.println("Couldn't get event id after adding event in db");
                }
            }
            return event;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        /*try {
            //if(result != null) result.close();
            if(statement != null) statement.close();
            if(connection != null) connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
		*/
        return null;
    }
}
