import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
		
public class ModifyUserInDb {
	public static void main(String[] args) {
		String mods[] = {"username", "willy", "email", "example@gmail.com"};
		modifyUser(mods, "1");
	}
	/*
	 * Update any user information except id 
	 * Strint[] mods (modifications), format: [col1_name, col1_value, col2_name, col2_value ...]
	 * example format: modifications = [username, jon snow, password, urabastardjonsnow]
	 */		
	private static void modifyUser(String[] mods, String userID) {
		//if invalid input
		if (mods.length < 2 || mods.length % 2 != 0 || mods == null || userID == null)  {
			System.out.println("invalid input: make sure to follow proper mods format" 
				       + "and inputs are not null.");
			return;
		}

		
		DataSource ds = null;  //mysql schedule database
		ds = DataSourceFactory.getDataSource();
		if (ds == null) {
			System.out.println("null data source");
			return;
		}					
		Connection connection = null;  //used to connect to database
		Statement statement = null;  //statement to enter command
		ResultSet result = null;  //output after query
		try {
			//set up connection
			connection = ds.getConnection();
			//create statement
			//statement = connection.createStatement();
			statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, 
					ResultSet.CONCUR_UPDATABLE);	
			//query  database
			String query = "SELECT *" 
					+ " from Users where id=" + userID;
			System.out.println(query);
			result = statement.executeQuery(query);
			result.next();
			//modify result
			int length = mods.length;
			System.out.println(length);
			for (int i = 0; i < length; i+=2) {	
				if (!mods[i].equals("username") && !mods[i].equals("fullname") 
					&& !mods[i].equals("password") && !mods[i].equals("email") 
					&& !mods[i].equals("phone_number")) {
					//invalid input
					//rollback
					System.out.println("INVALID INPUT");
					return;
				} else {
					result.updateString(mods[i], mods[i+1]);
				}
			}

			result.updateRow();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(result != null) result.close();
				if(statement != null) statement.close();
				if(connection != null) connection.close();

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}

