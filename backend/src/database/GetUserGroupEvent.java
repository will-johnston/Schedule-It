package database;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by williamjohnston on 10/26/17.
 */

public class GetUserGroupEvent {
    public static int getuge(int groupID, int userID) {
        MysqlConnectionPoolDataSource ds = null;  //datasource to connect to database

        Connection connection = null;
        Statement statement = null;
        ResultSet result = null;
        boolean ret = true;
        int eventID = -1;
        try {
            //call the DataSourceFactory class to create a pooled datasource
            ds = DataSourceFactory.getDataSource();
            //check for potential failed connection
            if (ds == null) {
                ret = false;
            }

	    connection = ds.getConnection();
            //delete row, if it is there, for userID and groupID
            String query = "select * from cb_user_group_event_junction where userID=" + userID  + " and groupID=" + groupID;
            statement = connection.createStatement();
            result = statement.executeQuery(query);

            //this group exists
            if (result.isBeforeFirst());
	    	result.next();
                eventID = result.getInt(3);
            //sanity check

            if (result.isBeforeFirst()) {
                System.out.println("ERROR: found multiple groups with same id.");
                return -1;
            }


        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if(result != null) result.close();
                if(statement != null) statement.close();
                if(connection != null) connection.close();
            } catch (SQLException etwo) {
                eventID = -1;
                etwo.printStackTrace();
            }
            return eventID;
        }
        try {
            if(result != null) result.close();
            if(statement != null) statement.close();
            if(connection != null) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            eventID = -1;
        }
        return eventID;
    }
}
