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
    /* Proposed changes
     * Make SQL handle setting time
      * */
    public static boolean setMessage(Object[] args) {
        MysqlConnectionPoolDataSource ds = null;  //datasource to connect to database
						
		Connection connection = null;
		Statement statement = null; 
		ResultSet result1 = null;
		boolean ret = true;
        try {
            //Store args in logical/readable variables
            String username = (String)args[0];
            //String groupID = args[1];
			int groupID = 1;
            String line = (String) args[2];

            //Call DataSourceFactor
			ds = DataSourceFactory.getDataSource();

			//Check for potential failed connection
			if (ds == null) {
				ret = false;
			}	

            //Acquire datasource object
			connection = ds.getConnection();
			
            //Form query 
			//String sqlInsert = "INSERT INTO chat_line (username, groupID, time, line) VALUES ('"+username+"','"+groupID+"','"+time+"','"+line+"')";
			String sqlInsert = String.format("INSERT INTO chat_line VALUES('%s',%d,NULL,%s)", username, groupID, line);
			statement = connection.createStatement();
			statement.executeUpdate(sqlInsert);
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