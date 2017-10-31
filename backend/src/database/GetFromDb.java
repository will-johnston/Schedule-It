package database;
/*
Created by Ryan
 */
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.sql.DataSource;
import management.Tracker;
import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

//TODO Merge this with RetrieveUserInfo
public class GetFromDb {
    //return {id, username, password, name, email, phone}
    public static String[] getUserFromName(String username) {
        MysqlConnectionPoolDataSource ds = null;  //mysql schedule database
        ds = DataSourceFactory.getDataSource();
        if (ds == null) {
            System.out.println("null data source getUserFromName");
            return null;
        }
        Connection connection = null;  //used to connect to database
        Statement statement = null;  //statement to enter command
        ResultSet result = null;  //output after query
        String[] results = new String[6];
        try {
            //set up connection
            connection = ds.getConnection();
            //create statement
            //statement = connection.createStatement();
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            //query  database
            String query = "SELECT *"
                    + " from users where username='" + username + "'";
            System.out.println(query);
            result = statement.executeQuery(query);
            //System.out.println("Checking for null");
            /*if (result == null) {
                return null;
            }*/
            result.first();


            results[0] = result.getString("id");
            results[1] = result.getString("username");
            results[2] = result.getString("password");
            results[3] = result.getString("fullname");
            results[4] = result.getString("email");
            results[5] = result.getString("phone_number");
            return results;
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
    public static ArrayList<String> getFriendIds(int id) {
        MysqlConnectionPoolDataSource ds = null;  //mysql schedule database
        ds = DataSourceFactory.getDataSource();
        if (ds == null) {
            System.out.println("null data source getFriends");
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
            System.out.println("Formatting query getFriends");
            String query = String.format("select * from friends where userID_1=%d OR userID_2=%d;", id, id);
            System.out.println(query);
            result = statement.executeQuery(query);
            System.out.println("Checking for null");
            ArrayList<String> list = new ArrayList<String>();
            while (result.next()) {
                int user1 = result.getInt("userID_1");
                int user2 = result.getInt("userID_2");
                if (user1 == id) {
                    //list.add(user2);
                    String username = getNameFromId(user2, connection, ds);
                    if (username == null) {
                        System.out.println("Couldn't get username from id " + user2);
                        continue;
                    }
                    list.add(username);
                    System.out.println("Adding " + user2 + " with name " + username);
                }
                else {
                    //list.add(user1);
                    String username = getNameFromId(user1, connection, ds);
                    if (username == null) {
                        System.out.println("Couldn't get username from id " + user1);
                        continue;
                    }
                    list.add(username);
                    System.out.println("Adding " + user1 + " with name " + username);
                }
            }

            return list;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if(result != null) result.close();
            if(statement != null) statement.close();
            if(connection != null) connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //returns id, name, creator, imagePath
    public static Object[] getGroupInfo(int groupid) {
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
            String query = String.format("SELECT * FROM groups WHERE groupid=%d", groupid);
            System.out.println(query);
            result = statement.executeQuery(query);
            System.out.println("Checking for null");
            /*if (result == null) {
                return null;
            }*/
            //groupid | name | creatorid | image_path
            if (result.next()) {
                System.out.println("Getting values");
                int grupid = result.getInt("groupid");
                String groupname = result.getString("name");
                int creatorid = result.getInt("creatorid");
                String imagePath = result.getString("image_path");
                return new Object[] { grupid, groupname, creatorid, imagePath};
            }
            else {
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if(result != null) result.close();
            if(statement != null) statement.close();
            if(connection != null) connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static ArrayList<String> getGroupMembers(int groupid) {
        MysqlConnectionPoolDataSource ds = null;  //mysql schedule database
        ds = DataSourceFactory.getDataSource();
        if (ds == null) {
            System.out.println("null data source getFriends");
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
            System.out.println("Formatting query getFriends");
            //select * from group_user_junction where groupID=3;
            String query = String.format("SELECT * FROM group_user_junction WHERE groupID=%d;", groupid);
            System.out.println(query);
            result = statement.executeQuery(query);
            System.out.println("Checking for null");
            ArrayList<String> list = new ArrayList<String>();
            while (result.next()) {
                int userid = result.getInt("userID");
                //get username from id
                String username = getNameFromId(userid, connection, ds);
                if (username == null) {
                    System.out.println("Couldn't get username from id: " + userid);
                }
                else {
                    list.add(username);
                }
            }

            return list;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if(result != null) result.close();
            if(statement != null) statement.close();
            if(connection != null) connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static ArrayList<Group> getGroups(int userid, Tracker tracker) {
        MysqlConnectionPoolDataSource ds = null;  //mysql schedule database
        ds = DataSourceFactory.getDataSource();
        if (ds == null) {
            System.out.println("null data source getFriends");
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
            System.out.println("Formatting query getFriends");
            //select * from group_user_junction where groupID=3;
            String query = String.format("SELECT * FROM group_user_junction WHERE userID=%d;", userid);
            System.out.println(query);
            result = statement.executeQuery(query);
            System.out.println("Checking for null");
            ArrayList<Group> list = new ArrayList<Group>();
            int results = 0;
            while (result.next()) {
                System.out.println("Result: " + results);
                int groupid = result.getInt("groupID");
                //get username from id
                try {
                    System.out.println("Trying to add " + groupid);
                    Group group = Group.fromDatabase(tracker, groupid);
                    if (group == null) {
                        System.out.println("Couldn't get group from id: " + groupid);
                    }
                    else {
                        System.out.println("add " + groupid);
                        list.add(group);
                    }
                }
                catch (Exception e) {
                    System.out.println("Caught exception");
                    continue;
                }
                results++;
            }
            System.out.println("Results: " + list.size());
            return list;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getNameFromId(int id, Connection connection, DataSource ds) {
        if (ds == null) {
            System.out.println("null data source getUserFromName");
            return null;
        }
        Statement statement = null;
        ResultSet result = null;
        try {
            //set up connection
            connection = ds.getConnection();
            //create statement
            //statement = connection.createStatement();
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            //query  database
            System.out.println("Formatting query getName");
            String query = String.format("SELECT * from users where id=%d", id);
            System.out.println(query);
            result = statement.executeQuery(query);
            System.out.println("Checking for null");
            if (result.next()) {
                /*try {
                    if(result != null) result.close();
                    if(statement != null) statement.close();
                    if(connection != null) connection.close();

                } catch (SQLException e) {
                    e.printStackTrace();
                }*/
                return result.getString("username");
            }
            else {
                /*try {
                    if(result != null) result.close();
                    if(statement != null) statement.close();
                    if(connection != null) connection.close();

                } catch (SQLException e) {
                    e.printStackTrace();
                }*/
                return null;
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
        }*/

    }
    //return {id, username, password, name, email, phone}
    public static String[] getUserFromId(int id) {
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
            String query = String.format("SELECT * from users where id=%d", id);
            System.out.println(query);
            result = statement.executeQuery(query);
            System.out.println("Checking for null");
            /*if (result == null) {
                return null;
            }*/
            result.first();

            String[] results = new String[6];
            results[0] = result.getString("id");
            results[1] = result.getString("username");
            results[2] = result.getString("password");
            results[3] = result.getString("fullname");
            results[4] = result.getString("email");
            results[5] = result.getString("phone_number");
            return results;

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
    public static boolean usernameExists(String username) {
        MysqlConnectionPoolDataSource ds = null;  //mysql schedule database

        ds = DataSourceFactory.getDataSource();
        if (ds == null) {
            System.out.println("null data source getFriends");
            return false;
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
            System.out.println("Formatting query usernameExists");
            //select * from group_user_junction where groupID=3;
            String query = String.format("SELECT * FROM users WHERE username='%s';", username);
            System.out.println(query);
            result = statement.executeQuery(query);
            System.out.println("Checking for null");
            ArrayList<Group> list = new ArrayList<Group>();
            boolean exists = false;
            while (result.next()) {
                String gotname = result.getString("username");
                if (username.equals(gotname)) {
                    exists = true;
                }
            }

            return exists;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if(result != null) result.close();
                if(statement != null) statement.close();
                if(connection != null) connection.close();

            } catch (SQLException etwo) {
                etwo.printStackTrace();
            }
            System.out.println("Returning false");
            return false;
        }
    }
    public static Integer[] getEventIds(int id) {
        MysqlConnectionPoolDataSource ds = null;  //mysql schedule database

        ds = DataSourceFactory.getDataSource();
        if (ds == null) {
            System.out.println("null data source getFriends");
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
            System.out.println("Formatting query usernameExists");
            String query = String.format("SELECT * FROM events WHERE groupID='%d';", id);
            System.out.println(query);
            result = statement.executeQuery(query);
            System.out.println("Checking for null");
            ArrayList<Integer> list = new ArrayList<Integer>();
            while (result.next()) {
                list.add(result.getInt("eventID"));
            }
            Integer[] arr = new Integer[list.size()];
            list.toArray(arr);
            return arr;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if(result != null) result.close();
                if(statement != null) statement.close();
                if(connection != null) connection.close();

            } catch (SQLException etwo) {
                etwo.printStackTrace();
            }
            System.out.println("Returning false");
            return null;
        }
    }
    public static Event getEvent(int id) {
        MysqlConnectionPoolDataSource ds = null;  //mysql schedule database

        ds = DataSourceFactory.getDataSource();
        if (ds == null) {
            System.out.println("null data source getFriends");
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
            String query = String.format("SELECT * FROM events WHERE eventID=%d;", id);
            result = statement.executeQuery(query);
            if (result.next()) {
                //name, type, desc, image
                String name = result.getString("event_name");
                String type = result.getString("type");
                String desc = result.getString("description");
                String image = result.getString("image_path");
                Event event = new Event(id, name, type, desc, image);
                event.setAddress(result.getString("address"));
                event.setGroupID(result.getInt("groupID"));
                event.setTime(result.getTimestamp("time"));
                event.setAccept(result.getString("accept"));
                event.setDecline(result.getString("decline"));
                event.setMaybe(result.getString("maybe"));
                event.setCreated(result.getTimestamp("created"));
                return event;
            }
            else {
                System.out.println("No events found");
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if(result != null) result.close();
                if(statement != null) statement.close();
                if(connection != null) connection.close();

            } catch (SQLException etwo) {
                etwo.printStackTrace();
            }
            System.out.println("Returning false");
            return null;
        }
    }
}
