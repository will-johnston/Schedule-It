package database;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;		
import javax.sql.DataSource;
import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
		
public class AddUserToDb {
	
	public static void main(String[] args) {
		addUser("CLARENCE", null, null, null, null);
	}		
	//This function takes adds a user to the mysql database		
	public static boolean addUser(String username,String fullname,String  password,String email,String phoneNumber) {
		MysqlConnectionPoolDataSource ds = null;  //datasource to connect to database
						
		Connection connection = null;
		Statement statement = null; 
		ResultSet result = null;
		try {
			//call the DataSourceFactory class to create a pooled datasource 
			ds = DataSourceFactory.getDataSource();	
			//check for potential failed connection
			if (ds == null) {
				return false;
			 
			}	
			connection = ds.getConnection(); //acquire datasource object
			//check to see if username is taken.
			String query = "SELECT * FROM users WHERE username='" + username + "'";
			statement = connection.createStatement();
			result = statement.executeQuery(query);
			
			if (!result.isBeforeFirst()) {	
			//perform add user functionality
			String update = "INSERT INTO users VALUES(null " +  ",'" + username + "','"  + fullname + "','" + password + "','" + email + "','" + phoneNumber + "')";
			statement = connection.createStatement();
			//send an add user query to the database
			int ex = statement.executeUpdate(update);
			} else {
				//duplicate user send false
				System.out.println("shit");
				return false;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if(result != null) result.close();
				if(statement != null) statement.close();
				if(connection != null) connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}
	}
}
