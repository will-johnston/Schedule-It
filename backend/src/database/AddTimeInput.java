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
  /*public static void main(String[] args) {

	boolean ret = AddTimeInput.addInput(15,1, "2017-11-01 20:00:00");	
	System.out.println(ret);
  }*/
  public static boolean addInput(int groupID, int eventID, String datetime) {

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
            //perform add user functionality
            String update = "INSERT INTO time_inputs VALUES(null, "  + groupID + ", "  + "'" + datetime + "', " + eventID + ")";

            statement = connection.createStatement();
            //send an add user query to the database
            int ex = statement.executeUpdate(update);


        } catch (SQLException e) {
            e.printStackTrace();
	          ret = false;
            try {
                if(result != null) result.close();
                if(statement != null) statement.close();
                if(connection != null) connection.close();
            } catch (SQLException etwo) {
                etwo.printStackTrace();
		            ret = false;
            }
            return ret;
        }
        try {
            if(result != null) result.close();
            if(statement != null) statement.close();
            if(connection != null) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
	          ret = false;
        }
        return ret;
    }
}
