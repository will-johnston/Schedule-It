package database;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by williamjohnston on 10/26/17.
 */

public class SetUserGroupEvent {
    public static void main(String[] args) {
	    boolean ret = SetUserGroupEvent.setuge(27,16,-2);
    	    System.out.println(ret);
    }
    public static boolean setuge(int groupID, int userID, int eventID) {
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
	    connection = ds.getConnection();
            //TODO: make sure event is valid

	    //delete row, if it is there, for userID and groupID
            String delete = "delete from cb_user_group_event_junction where (userID =" + userID +" and groupID=" + groupID + ")";
            statement = connection.createStatement();
            int ex = statement.executeUpdate(delete);
            
	    //insert row to db
            String update = "INSERT INTO cb_user_group_event_junction VALUES(" + userID +  "," + groupID + ","  + eventID  + ")";
            statement = connection.createStatement();
            ex = statement.executeUpdate(update);

        } catch (SQLException e) {
	    ret = false;
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
	    ret = false;
            e.printStackTrace();
        }
        return ret;
    }
}
