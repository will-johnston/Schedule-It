package database;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

import java.sql.*;

public class EventPutter {
    public static boolean addEvent(Event event) {
        if (event == null) {
            return false;
        }
        //gets all the event ids connected to the user
        MysqlConnectionPoolDataSource ds = null;  //mysql schedule database
        ds = DataSourceFactory.getDataSource();
        if (ds == null) {
            System.out.println("null data source getUserFromName");
            return false;
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
                return false;
            }
            else {
                return true;
            }

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
        return false;
    }
}
