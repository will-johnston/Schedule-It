package database;
import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by williamjohnston on 9/30/17.
 */
public class RemoveUserFromDb {

    public static void main(String[] args) {
        removeUser("bro");
    }

    public static boolean removeUser(String username) {
        MysqlConnectionPoolDataSource ds = null;  //datasource to connect to database

        Connection connection = null;
        Statement statement = null;
        ResultSet result = null;
        try {
            //call the DataSourceFactory class to create a pooled datasource
            ds = DataSourceFactory.getDataSource();
            //check for potential failed connection
            if (ds == null) {
                return false;

            }
            connection = ds.getConnection(); //acquire datasource object


            //get ID of user
            int id;
            String query = "SELECT id from users where username=" + username;
            statement = connection.createStatement();
            result = statement.executeQuery(query);

            if (result.next()) {
                id = result.getInt(1);
            } else {
                return false;
            }

            //remove all friendships user has
            String removeFriendships = "DELETE FROM friends WHERE userID_1 =" + id + " OR userID_2=" + id;
            statement.executeUpdate(removeFriendships);
	    //remove all groups user created
	    
	    //remove user from all groups
            //delete user from users table
            String removeUser = "DELETE FROM users where username=" + username;
            statement = connection.createStatement();
            statement.executeUpdate(removeUser);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if(result != null) result.close();
                if(statement != null) statement.close();
                if(connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }
}
