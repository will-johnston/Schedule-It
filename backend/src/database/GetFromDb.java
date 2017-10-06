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

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if(result != null) result.close();
                if(statement != null) statement.close();
                if(connection != null) connection.close();

            } catch (SQLException r) {
                r.printStackTrace();
            }
            return null;
        }  try {
                if(result != null) result.close();
                if(statement != null) statement.close();
                if(connection != null) connection.close();
        }
        return results;
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
                return result.getString("username");
            }
            else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        try {
            if(result != null) result.close();
            if(statement != null) statement.close();
            if(connection != null) connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;

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
        try {
            if(result != null) result.close();
            if(statement != null) statement.close();
            if(connection != null) connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
