package database;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;		
import javax.sql.DataSource;
import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import java.io.*;
import java.util.ArrayList;

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
	public static void main(String[] args) {
		ArrayList arr = Messages.getMessage(1);
		for (int i = 0; i < arr.size(); i++) {
			System.out.println(arr.get(i));
		}
	}

	public static boolean setMessage(String username, int groupID, String line) {
        MysqlConnectionPoolDataSource ds = null;  //datasource to connect to database
						
		Connection connection = null;
		Statement statement = null; 
		ResultSet result1 = null;
		boolean ret = true;
        try {

            //Call DataSourceFactor
			ds = DataSourceFactory.getDataSource();

			//Check for potential failed connection
			if (ds == null) {
				ret = false;
			}	

            //Acquire datasource object
			connection = ds.getConnection();
			
            //Form query 
			String sqlInsert = "INSERT INTO chat_line (username, groupID, line) VALUES ('"+username+"','"+groupID+"','"+line+"')";
			//String sqlInsert = String.format("INSERT INTO chat_line (username, groupID, time, line) VALUES('%s',%d,NULL,%s)", username, groupID, line);
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

    public static ArrayList<String> getMessage(int groupID) {
		MysqlConnectionPoolDataSource ds = null;  //datasource to connect to database

		Connection connection = null;
		Statement statement = null;
		ResultSet result1 = null;
		ArrayList<String> chat = null;
		try {

			//Call DataSourceFactory
			ds = DataSourceFactory.getDataSource();

			//Check for potential failed connection
			if (ds == null) {
				chat = null;
			}

			//Acquire datasource object
			connection = ds.getConnection();

			//Form query
			String query = "select line from chat_line where groupID=" + groupID +  " order by time asc";
			//String sqlInsert = String.format("INSERT INTO chat_line (username, groupID, time, line) VALUES('%s',%d,NULL,%s)", username, groupID, line);
			statement = connection.createStatement();
			result1 = statement.executeQuery(query);
			chat = new ArrayList<String>();
			while (result1.next()) {
				String line = result1.getString(1);
				chat.add(line);
			}
			//ret = (String[]) chat.toArray();

		} catch (SQLException e) {
			e.printStackTrace();
			chat = null;
			try {
				if(result1 != null) result1.close();
				if(statement != null) statement.close();
				if(connection != null) connection.close();
			} catch (SQLException etwo) {
				etwo.printStackTrace();
				chat = null;
			}
			return chat;
		}
		try {
			if(result1 != null) result1.close();
			if(statement != null) statement.close();
			if(connection != null) connection.close();

		} catch (SQLException e) {
			e.printStackTrace();
			chat = null;
		}
		return chat;
	}
}
