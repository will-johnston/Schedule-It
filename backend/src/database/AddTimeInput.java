package database;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by williamjohnston on 10/26/17.
 */

public class AddTimeInput {
    public static boolean addInput(int groupID, int userID, int eventID, String datetime) {
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


            //perform add user functionality
            String update = "INSERT INTO cb_user_group_event_junction VALUES(" + userID +  "," + groupID + ","  + eventID  + ")";
            statement = connection.createStatement();
            //send an add user query to the database
            int ex = statement.executeUpdate(update);


        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if(result != null) result.close();
                if(statement != null) statement.close();
                if(connection != null) connection.close();
            } catch (SQLException etwo) {
                etwo.printStackTrace();
            }
            return ret;
        }
        try {
            if(result != null) result.close();
            if(statement != null) statement.close();
            if(connection != null) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }
}
