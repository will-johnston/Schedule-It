package database;
import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Created by williamjohnston on 9/30/17.
 */
public class FindUsersFriends {

    /*public static void main(String[] args) {
	String[] friends = findFriends("will");
	for (String str: friends) {
		System.out.println(str);
	}

    }*/

    public static String[] findFriends(String username) {
        MysqlConnectionPoolDataSource ds = null;  //datasource to connect to database

        Connection connection = null;
        Statement statement = null;
        ResultSet result = null;
        String[] friends = null;
        try {
            //call the DataSourceFactory class to create a pooled datasource
            ds = DataSourceFactory.getDataSource();
            //check for potential failed connection
            if (ds == null) {
                return null;

            }
            connection = ds.getConnection(); //acquire datasource object
            //get ids of users
            int id = getId(username, connection);
            //handle bad query
            if (id == -1) {
                return null;
            }

            LinkedList<Integer> friendsids = new LinkedList<Integer>();
            //perform add friendship functionality
            String query = "SELECT * FROM friends WHERE userID_1=" + id + " OR userID_2=" + id;
            statement = connection.createStatement();
            //send an add user query to the database
            result = statement.executeQuery(query);
            if (result.isBeforeFirst()) {
                //this user has friends
                while (result.next()) {
                    int id1 = result.getInt(1);
                    int id2 = result.getInt(2);
                    if (id1 == id) {
                        friendsids.addFirst(id2);
                    } else {
                        friendsids.addFirst(id1);
                    }
                }
            }
            //now need to get username for each id
            friends = getUsernames(friendsids, connection);
            if (friends == null) {
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if(result != null) result.close();
                if(statement != null) statement.close();
                if(connection != null) connection.close();
                return friends;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public static int getId(String username, Connection connection) {
        int id = -1;
        Statement statement = null;
        ResultSet result = null;

        try {
            String query = "SELECT id FROM users WHERE username='" + username + "'";
            statement = connection.createStatement();
            result = statement.executeQuery(query);
            if (result.isBeforeFirst()) {
                //we have found the user
                result.next();
                id = result.getInt(1);
                return id;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;

    }

    public static String[] getUsernames(LinkedList<Integer> ids, Connection connection) {
        int size = ids.size();
        Statement statement = null;
        ResultSet result = null;
        String usernames[] = new String[size];

        //find all users, add to string array
        for (int i = 0; i < size; i++) {
            try {
                String query = "SELECT username FROM users WHERE id='" + ids.get(i) + "'";
                statement = connection.createStatement();
                result = statement.executeQuery(query);
                if (result.next()) {
                    usernames[i] = result.getString(1);
                } else {
                    return null;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
	Arrays.sort(usernames);
        return usernames;
    }
}
