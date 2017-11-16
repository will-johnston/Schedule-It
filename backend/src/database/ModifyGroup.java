package database;
import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by williamjohnston on 10/3/17.
 */
public class ModifyGroup {
    
    public static boolean changeGroupName(int groupID, String newName) {
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
                System.out.println("ERROR: could get data source");
                ret = false;
            }
            connection = ds.getConnection(); //acquire datasource object
            if (connection == null) {
                System.out.println("ERROR: could not connect to data source");
                ret = false;
            }

            //change group name
            String update = "UPDATE groups set name ='" + newName + "' WHERE groupid=" + groupID;
            statement = connection.createStatement();
            statement.executeUpdate(update);

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
        }
        try {
            if (result != null) result.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;

    }

    public static boolean addUserToGroup(int groupID, int userID) {
        MysqlConnectionPoolDataSource ds = null;  //datasource to connect to database
        Connection connection = null;
        Statement statement = null;
        ResultSet result = null;
        try {
            //call the DataSourceFactory class to create a pooled datasource
            ds = DataSourceFactory.getDataSource();
            //check for potential failed connection
            if (ds == null) {
                System.out.println("ERROR: could not get data source");
                return false;
            }

            connection = ds.getConnection(); //acquire datasource object
            if (connection == null) {
                System.out.println("ERROR: could not connect to data source");
                return false;
            }

            //make sure user is not already in the group
            String queryJT = "SELECT * from group_user_junction WHERE userID =" + userID + " AND groupID=" + groupID;
            statement = connection.createStatement();
            result = statement.executeQuery(queryJT);
            if (result.isBeforeFirst()) {
                //user is in group. No additional work required.
                return true;
            }

            //add user to group
            String update = "INSERT INTO group_user_junction (userID, groupID) VALUES(" + userID + "," + groupID + ")";
            statement = connection.createStatement();
            statement.executeUpdate(update);

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (result != null) result.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public static boolean removeUserFromGroup(int groupID, int userID) {
        MysqlConnectionPoolDataSource ds = null;  //datasource to connect to database
        Connection connection = null;
        Statement statement = null;
        ResultSet result = null;
        try {
            //call the DataSourceFactory class to create a pooled datasource
            ds = DataSourceFactory.getDataSource();
            //check for potential failed connection
            if (ds == null) {
                System.out.println("ERROR: could not get data source");
                return false;
            }

            connection = ds.getConnection(); //acquire datasource object
            if (connection == null) {
                System.out.println("ERROR: could not connect to data source");
                return false;
            }

            //make sure user is in the group
            String queryJT = "SELECT * from group_user_junction WHERE userID =" + userID + " AND groupID=" + groupID;
            statement = connection.createStatement();
            result = statement.executeQuery(queryJT);
            if (!result.isBeforeFirst()) {
                //user is NOT in group. No additional work required.
                return true;
            }

            //make sure user is not creator
            String queryGroup = "SELECT * from groups WHERE creatorID =" + userID + " AND groupid=" + groupID;

            statement = connection.createStatement();
            result = statement.executeQuery(queryGroup);
            if (result.isBeforeFirst()) {
                System.out.println("ERROR: Cannot remove creator of group.");
                return false;
            }

            //remove user from group
            String update = "DELETE FROM group_user_junction WHERE userID =" + userID + " AND groupID=" + groupID;
            statement = connection.createStatement();
            statement.executeUpdate(update);

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (result != null) result.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public static boolean addAdminPrivileges(int groupID, int userID) {
        MysqlConnectionPoolDataSource ds = null;  //datasource to connect to database
        Connection connection = null;
        Statement statement = null;
        ResultSet result = null;

        try {
            //call the DataSourceFactory class to create a pooled datasource
            ds = DataSourceFactory.getDataSource();
            //check for potential failed connection
            if (ds == null) {
                System.out.println("ERROR: could not get data source");
                return false;
            }

            connection = ds.getConnection(); //acquire datasource object
            if (connection == null) {
                System.out.println("ERROR: could not connect to data source");
                return false;
            }

            //make sure user is in the group
            String queryJT = "SELECT * from group_user_junction WHERE userID =" + userID + " AND groupID=" + groupID;
            statement = connection.createStatement();
            result = statement.executeQuery(queryJT);
            if (!result.isBeforeFirst()) {
                //user is NOT in group. No additional work required.
                System.out.println("ERROR: User is not in the group.");
                return false;
            }

            //make sure user is not in admins already
            String queryGroupAdmins = "SELECT * from groupAdmins WHERE userID =" + userID + " AND groupID=" + groupID;
            statement = connection.createStatement();
            result = statement.executeQuery(queryGroupAdmins);
            if (result.isBeforeFirst()) {
                System.out.println("ERROR: Already an admin.");
                return false;
            }

            //get adminID
            String getAdminID = "SELECT adminID FROM groupAdmins WHERE groupID=" + groupID;
            statement = connection.createStatement();
            result = statement.executeQuery(getAdminID);
            int adminID = -1;
            if (result.next()) {
                adminID = result.getInt(1);
            }
            //add user to admins table
            if (adminID > 0) {
                String update = "INSERT INTO groupAdmins VALUES(null," + groupID + "," + userID + ")";
                statement = connection.createStatement();
                statement.executeUpdate(update);
            } else {
                System.out.println("ERROR: Could not find group admin table");
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        try {
            if (result != null) result.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    public static boolean removeAdminPrivileges(int groupID, int userID) {
        MysqlConnectionPoolDataSource ds = null;  //datasource to connect to database
        Connection connection = null;
        Statement statement = null;
        ResultSet result = null;
        try {
            //call the DataSourceFactory class to create a pooled datasource
            ds = DataSourceFactory.getDataSource();
            //check for potential failed connection
            if (ds == null) {
                System.out.println("ERROR: could not get data source");
                return false;
            }

            connection = ds.getConnection(); //acquire datasource object
            if (connection == null) {
                System.out.println("ERROR: could not connect to data source");
                return false;
            }

            //remove admin
            String update = "DELETE from groupAdmins where groupID=" + groupID + " AND userID=" + userID;
            statement = connection.createStatement();
            statement.executeUpdate(update);

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        try {
            if (result != null) result.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
