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
    //return {id, username, password, name, email, phone, notif_pref}
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
        String[] results = new String[8];
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
            results[6] = result.getString("notif_pref_group");
            results[7] = result.getString("image_path");
            System.out.println("Pref group: " + results[6]);
            close(result, statement, connection);
            return results;
        }
        catch (SQLException e) {
            e.printStackTrace();
            try {
                close(result, statement, connection);

            } catch (SQLException r) {
                r.printStackTrace();
            }
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
            close(result, statement, connection);
            return list;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                close(result, statement, connection);
            }
            catch (SQLException etwo) {
                etwo.printStackTrace();
                return null;
            }
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

    //returns id, name, creator, imagePath, noadmins
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
                int noadmins = result.getInt("noadmins");
                close(result, statement, connection);
                return new Object[] { grupid, groupname, creatorid, imagePath, noadmins};
            }
            else {
                close(result, statement, connection);
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            close(result, statement, connection);

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
            close(result, statement, connection);
            return list;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            close(result, statement, connection);

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
            close(result, statement, connection);
            return list;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                close(result, statement, connection);
            }
            catch (SQLException etwo) {
                etwo.printStackTrace();
            }
            return null;
        }
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
                close(result, statement, connection);
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
                close(result, statement, connection);
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                close(result, statement, connection);
            }
            catch (SQLException etwo) {
                etwo.printStackTrace();
            }
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
    //return {id, username, password, name, email, phone, notif_pref}
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

            String[] results = new String[8];
            results[0] = result.getString("id");
            results[1] = result.getString("username");
            results[2] = result.getString("password");
            results[3] = result.getString("fullname");
            results[4] = result.getString("email");
            results[5] = result.getString("phone_number");
            results[6] = result.getString("notif_pref_group");
            results[7] = result.getString("image_path");
            close(result, statement, connection);
            return results;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                close(result, statement, connection);
            }
            catch (SQLException etwo) {
                etwo.printStackTrace();
            }
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
            close(result, statement, connection);
            return exists;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                close(result, statement, connection);

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
            close(result, statement, connection);
            return arr;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                close(result, statement, connection);

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
                int is_polling_users = result.getInt(6); //finds the is_polling users field
                boolean is_open_ended = false;
                if (is_polling_users != -1) {
                    is_open_ended = true;
                }
                Event event = new Event(id, name, type, desc, image, is_open_ended);
                event.setAddress(result.getString("address"));
                event.setGroupID(result.getInt("groupID"));
                event.setTime(result.getTimestamp("time"));
                event.setAccept(result.getString("accept"));
                event.setDecline(result.getString("decline"));
                event.setMaybe(result.getString("maybe"));
                event.setCreated(result.getTimestamp("created"));
                close(result, statement, connection);
                return event;
            }
            else {
                System.out.println("No events found");
                close(result, statement, connection);
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                close(result, statement, connection);
            } catch (SQLException etwo) {
                etwo.printStackTrace();
            }
            System.out.println("Returning false");
            return null;
        }
    }
    public static boolean noadmins(int groupid) {
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
            System.out.println("Formatting query noadmins");
            String query = String.format("SELECT noadmins FROM groups WHERE groupID=%d;", groupid);
            System.out.println(query);
            result = statement.executeQuery(query);
            if (result.next()) {
                close(result, statement, connection);
                return result.getBoolean("noadmins");
            }
            else {
                System.out.println("No rows were returned, returning false");
                close(result, statement, connection);
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                close(result, statement, connection);

            } catch (SQLException etwo) {
                etwo.printStackTrace();
            }
            System.out.println("Returning false");
            return false;
        }
    }
    public static void close(ResultSet result, Statement statement, Connection connection) throws SQLException {
        if(result != null) result.close();
        if(statement != null) statement.close();
        if(connection != null) connection.close();
    }
}
