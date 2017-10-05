package database;
import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by williamjohnston on 10/2/17.
 */
public class CreateGroup {
    public static boolean createGroup(String name, int creatorID) {
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

            //if userID already has a group with same name, reject.
            String query = "SELECT id FROM groups WHERE name='" + name + "' AND creatorID=" + creatorID;
            statement = connection.createStatement();
            result = statement.executeQuery(query);
            if (result.isBeforeFirst()) {
                //we have a group already
                System.out.println("ERROR: Already a group with same name and same creator");
                return false;
            }

            //create group
            String update = "INSERT INTO groups (name, creatorID) VALUES('" + name + "'," + creatorID + ")";
            statement = connection.createStatement();
            statement.executeUpdate(update);

            //get group id
            int groupID = getGroupId(name, creatorID, connection);


            //add user to group_user_junction with userID and groupID
            String updateJT = "INSERT INTO group_user_junction (userID, groupID) VALUES(" + creatorID + ", " + groupID + ")";
            statement = connection.createStatement();
            statement.executeUpdate(updateJT);


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

    public static int getUserId(String username, Connection connection) {
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


    public static int getGroupId(String name, int creator, Connection connection) {
        int id = -1;
        Statement statement = null;
        ResultSet result = null;

        try {
            String query = "SELECT id FROM groups WHERE name='" + name + "' AND creatorID=" + creator;
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

}
