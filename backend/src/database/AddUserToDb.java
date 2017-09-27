package database;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;		
import javax.sql.DataSource;
		
public class AddUserToDb {
	/*public static void main(String[] args) {
		for (String str: args) {
			System.out.println(str);
		}
		String id = (args[0]);
		String username = args[1];
		String fullname = args[2];
		String password = args[3];
		String email = args[4];
		String phoneNumber = args[5];		
		addUser(id, username, fullname, password, email, phoneNumber);
	}*/
				
	public static void addUser(String id, String username, String fullname, String password, String email,
	String phoneNumber) {
		DataSource ds = null;
		ds = data_source_factory.get_data_source();
		if (ds == null) {
			System.out.println("null data source");
			return;
		}					
		Connection connection = null;
		Statement statement = null;
		ResultSet result = null;
		try {
			connection = ds.getConnection();
			String query = "INSERT INTO Users VALUES('" + id +  "','" + username + "','"  + fullname + "','" + password + "','" + email + "','" + phoneNumber + "')";
			System.out.println(query);
			statement = connection.createStatement();
			int ex = statement.executeUpdate(query);
			/*while(result.next()){
				System.out.println(result.getStatement());       
			}*/
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
