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
        removeUser("user1");
    }

    public static boolean removeUser(String username) {
        MysqlConnectionPoolDataSource ds = null;  //datasource to connect to database

        Connection connection = null;
        Statement statement = null;
        ResultSet result = null;
        boolean ret = true;
        try {
            //call the DataSourceFactory class to create a pooled datasource
            ds = DataSourceFactory.getDataSource();
            //check for potential failed connection
            if (ds == null) {
                ret = false;

            }
            connection = ds.getConnection(); //acquire datasource object


            //get ID of user
            int id = -1;
            String query = "SELECT id from users where username='" + username + "'";
            statement = connection.createStatement();
            result = statement.executeQuery(query);

            if (result.next()) {
                id = result.getInt(1);
            } else {
                ret = false;
            }
            if (id != -1) {
                //remove all friendships user has
                String removeFriendships = "DELETE FROM friends WHERE userID_1 =" + id + " OR userID_2=" + id;
                statement = connection.createStatement();
                statement.executeUpdate(removeFriendships);

                //remove user from all groups
                String removeFromGroups = "DELETE FROM group_user_junction WHERE userID=" + id;
                statement = connection.createStatement();
                statement.executeUpdate(removeFromGroups);
                //remove all members from  group and delete group
                String findGroupsCreated = "SELECT  groupid FROM groups WHERE creatorID=" + id;
                statement = connection.createStatement();
                result = statement.executeQuery(findGroupsCreated);
                if (result.isBeforeFirst()) {
                    while (result.next()) {
                        int groupid = result.getInt(1);
                        //remove all users in group user junction under that group id
                        DeleteGroup.deleteGroup(groupid, id);
                    }
                }
                //delete user from users table
                String removeUser = "DELETE FROM users where username='" + username + "'";
                statement = connection.createStatement();
                statement.executeUpdate(removeUser);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            ret = false;
            try {
                if (result != null) result.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException r) {
                r.printStackTrace();
            }
            return ret;
        } finally {
            try {
                if (result != null) result.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return true;
        }
    }
}
