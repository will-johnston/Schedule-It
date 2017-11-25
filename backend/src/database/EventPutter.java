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
            String values = format(event);
            if (values == null) {
                System.out.println("Failed to format values");
                return null;
            }
            String query = String.format("INSERT INTO events VALUES(%s);", values);
            System.out.println(query);
            int result = statement.executeUpdate(query);
            if (result == 0) {
                System.out.println("Didn't update anything");
                return null;
            }
            else {
                //get the eventID
                query = String.format("SELECT * FROM events WHERE event_name=%s AND groupid=%d AND description=%s AND time=%s;", resolveNull(event.getEvent_name()),
                        event.getGroupID(), resolveNull(event.getDescription()), resolveNull(event.getTime().toString()));
                ResultSet newevent = statement.executeQuery(query);
                if (newevent.next()) {
                    event.setEventID(newevent.getInt("eventID"));
                    System.out.println("New id is: " + event.getEventID());
                    return event;
                }
                else {
                    System.out.println("Couldn't get event id after adding event in db");
                    return null;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        /*try {
            //if(result != null) result.close();
            if(statement != null) statement.close();
            if(connection != null) connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
		*/
    }
    public static String format(Event event) {

        int is_open_ended = -1;
        if (event.getIs_open_ended() == true) {
            is_open_ended = 1;
        }
        //eventid, address, groupid, event_name, expiration_time, is_polling_users, image_path, type, time,
        // description, accept, decline, maybe, userid, created
        //                             ID    AD  ID  NM   EX  IS  IM   TP   TI   DE   AC   DC   MA  CREAT
        String values = String.format("NULL,%s,%d,%s,%s,%d,%s,%s,%s,%s,%s,%s,%s,NULL",
                resolveNull(event.getAddress()),
                event.getGroupID(),
                resolveNull(event.getEvent_name()),
                "NULL",
                is_open_ended,
                resolveNull(event.getImage_path()),
                resolveNull(event.getType()),
                resolveNull(event.getTime().toString()),
                resolveNull(event.getDescription()),
                resolveNull(event.getAcceptString()),
                resolveNull(event.getDeclineString()),
                resolveNull(event.getMaybeString()));
        return values;
    }
    public static String resolveNull(String value) {
        if (value == null) {
            return "NULL";
        }
        if (value.equals("null") || value.equals("NULL")) {
            return "NULL";
        }
        return String.format("'%s'", value);
    }
    public static boolean updateEvent(String name, String description, String date, String type, int eventid) {
        if (eventid == 0) {
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

            String query = String.format("UPDATE events %s WHERE eventID=%d;", makeMods(name, description, date,
                    type), eventid);
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
            return false;
        }
    }
    public static boolean remove(int eventid) {
        if (eventid == 0) {
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

            String query = String.format("DELETE FROM events WHERE eventID=%d;", eventid);
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
            return false;
        }
    }
    //name, description, time, type
    public static String makeMods(String name, String description, String time, String type) {
        if (name == null && description == null && time == null && type == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder("SET ");
        if (name != null) {
            builder.append(String.format("event_name='%s'", name));
            if (description != null || time != null || type != null) {
                builder.append(", ");
            }
        }
        if (description == null) {
            builder.append(String.format("description='%s'", description));
            if (time != null || type != null) {
                builder.append(", ");
            }
        }
        if (time == null) {
            builder.append(String.format("time='%s'", time));
            if (type != null) {
                builder.append(", ");
            }
        }
        if (type == null) {
            builder.append(String.format("type='%s'", type));
        }
        return builder.toString();
    }
}
