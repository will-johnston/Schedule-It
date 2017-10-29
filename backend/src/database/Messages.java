package database;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;		
import javax.sql.DataSource;
import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import java.io.*;

/*
    Class to handle storing messages sent and retrieving messages from the polling
 */
public class Messages {
    /*
        Function to store messages in the DB
     */
    public static boolean setMessage(String[] args) {
        MysqlConnectionPoolDataSource ds = null;  //datasource to connect to database
						
		Connection connection = null;
		Statement statement = null; 
		ResultSet result1 = null;
		boolean ret = true;
        try {
            //Store args in logical/readable variables
            String username = args[0];
            //String groupID = args[1]; USE THIS WHEN WE CAN AQUIRE CORRECT GROUPID
            String groupID = "1";
            String time = args[2];
            String line = args[3];

            //Call DataSourceFactor
			ds = DataSourceFactory.getDataSource();

			//Check for potential failed connection
			if (ds == null) {
				ret = false;
			}	

            //Acquire datasource object
			connection = ds.getConnection();
			
            //Form query 
            //--------------------REFINE THIS--------------------
			String sqlInsert = "INSERT INTO chat_line (username, groupID, time, line) VALUES ('"+username+"','"+groupID+"','"+time+"','"+line+"')";
			statement = connection.createStatement();
			statement.executeUpdate(sqlInsert);
            //--------------------REFINE THIS--------------------

        } catch (SQLException e) {
            e.printStackTrace();

			try {
				if(result1 != null) result1.close();
				if(statement != null) statement.close();
				if(connection != null) connection.close();
			} catch (SQLException etwo) {
				etwo.printStackTrace();
			}

			return ret;
        }
        try {
			if(result1 != null) result1.close();
			if(statement != null) statement.close();
			if(connection != null) connection.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ret;
    }

    /*public static String[] getMessage() {

    }*/
}