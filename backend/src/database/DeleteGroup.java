package database;
import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by williamjohnston on 10/4/17.
 */
public class DeleteGroup {
	public static void main(String[] args) {
		boolean bool = DeleteGroup.deleteGroup(16, 16);
		System.out.println(bool);
	}

    public static boolean deleteGroup(int groupID, int creatorID) {
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
                System.out.println("ERROR: could not get data source object");
                ret = false;
            }

            connection = ds.getConnection(); //acquire datasource object
            if (connection == null) {
                System.out.println("ERROR: could not connect to data source");
                ret = false;
            }
            //remove creator from groups (ModifyGroup.removeUserFromGroup does not allow removal of creator)
            String removeCreator = "DELETE FROM group_user_junction WHERE userID=" + creatorID + " AND groupID=" + groupID;
            statement = connection.createStatement();
            statement.executeUpdate(removeCreator);

            //remove all other users from group
            String queryAllGroupUsers = "SELECT userID FROM group_user_junction where groupID=" + groupID;
            statement = connection.createStatement();
            result = statement.executeQuery(queryAllGroupUsers);
            if (result.isBeforeFirst()) {
                //there are additional users in group
                while (result.next()) {
                    int userID = result.getInt(1);
                    ModifyGroup.removeUserFromGroup(groupID, userID);
                }
            }

            //delete group
            String removeGroup = "DELETE FROM groups where groupid=" + groupID;
            statement = connection.createStatement();
            statement.executeUpdate(removeGroup);

        } catch (SQLException e) {
            ret = false;
            e.printStackTrace();
            try {
                if (result != null) result.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException r) {
                r.printStackTrace();
            }
        }
        try {
            if (result != null) result.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        } catch (SQLException r) {
            r.printStackTrace();
        }
        return ret;
    }
}
