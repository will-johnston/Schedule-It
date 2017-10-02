import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

public class AddOrRemoveFriendsInDb {

    public static void main(String[] args) {
        addOrRemoveFriend("chico", "bro", 0);
    }
    //This function takes adds a user to the mysql database
    //int add: 0 if you want to delete a friendship, any other int value is used to add a friendship
    //returns true if friendship has been added or remove, or if the friendship already is up to date
    public static boolean addOrRemoveFriend(String user1, String user2, int add) {
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
            //get ids of users
            int ids[] = getIds(user1, user2, connection);
            //handle bad query
            if (ids == null) {
                return false;
            }
            //insert lowest id in userID_1 and highest in userID_2
            int id1;
            int id2;

            if (ids[0] < ids[1]) {
                id1 = ids[0];
                id2 = ids[1];
            } else {
                id1 = ids[1];
                id2 = ids[0];
            }

            //see if users are already friends
            String check = "SELECT * FROM friends WHERE userID_1=" + id1 + " AND userID_2=" + id2;
            statement = connection.createStatement();
            result = statement.executeQuery(check);
            if (result.next()) {
                //already friends, if trying to add, no work is necessary
                if (add != 0) {
                    return true;
                }
            } else {
                //not friends, if trying to remove, no work is necessary
                if (add == 0) {
                    return true;
                }
            }
            String update;
            if (add != 0) {
                //perform add friendship functionality
                update = "INSERT INTO friends VALUES(" + id1 + ", " + id2 + ")";
            } else {
                update = "DELETE FROM friends WHERE userID_1=" + id1 + " AND userID_2=" + id2;
            }
            statement = connection.createStatement();
            //send an add user query to the database
            statement.executeUpdate(update);

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

    public static int[] getIds(String user1, String user2, Connection connection) {
        int[] ids = new int[2];
        Statement statement = null;
        ResultSet result = null;

        try {
            String query = "SELECT id FROM users WHERE username='" + user1 + "' OR username='" + user2 + "'";
            statement = connection.createStatement();
            result = statement.executeQuery(query);
            if (result.isBeforeFirst()) {
                //we have at least one user
                result.next();
                ids[0] = result.getInt(1);
                if (result.next()) {
                    //both users exist
                    ids[1] = result.getInt(1);
                    return ids;
                }
            }
            //one or both users cannot be found.
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ids;

    }

}
