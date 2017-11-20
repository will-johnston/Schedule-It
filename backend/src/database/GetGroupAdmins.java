package database;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by williamjohnston on 11/16/17.
 */

public class GetGroupAdmins {

    public static ArrayList<Integer> getGroupAdmins(int groupID) {
        MysqlConnectionPoolDataSource ds = null;  //datasource to connect to database
        Connection connection = null;
        Statement statement = null;
        ResultSet result = null;
        ArrayList<Integer> admins = null;

        try {
            //call the DataSourceFactory class to create a pooled datasource
            ds = DataSourceFactory.getDataSource();
            //check for potential failed connection
            if (ds == null) {
                System.out.println("ERROR: could not get data source");
                return null;
            }

            connection = ds.getConnection(); //acquire datasource object
            if (connection == null) {
                System.out.println("ERROR: could not connect to data source");
                return null;
            }

            admins = new ArrayList<Integer>();

            //make sure user is in the group
            String queryAdmins = "SELECT userID from groupAdmins WHERE groupID=" + groupID;
            statement = connection.createStatement();
            result = statement.executeQuery(queryAdmins);

            if (!result.isBeforeFirst()) {
                System.out.println("ERROR: No group admins.");
                return null;
            } else {
                while (result.next()) {
                    //get next admin
                    int a = result.getInt(1);
                    admins.add(a);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        try {
            if (result != null) result.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();
            return admins;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }
}
