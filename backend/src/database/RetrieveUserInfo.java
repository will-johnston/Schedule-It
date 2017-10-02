import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

/**
 * Created by williamjohnston on 9/30/17.
 */
public class RetrieveUserInfo {

    public static void main(String[] args) {
	String[] info = retrieveUserInfo("fernando");
	System.out.println();
	for (String str: info) {
		System.out.println(str);
	}	

    }
    //returns: String[] of size 6, format={id, username, password, fullname, email, phone #}
    public static String[] retrieveUserInfo(String username) {
        MysqlConnectionPoolDataSource ds = null;  //datasource to connect to database
        Connection connection = null;
        Statement statement = null;
        ResultSet result = null;
        String[] info = new String[6];  //user info array
        try {
            //call the DataSourceFactory class to create a pooled datasource
            ds = DataSourceFactory.getDataSource();
            //check for potential failed connection
            if (ds == null) {
                return null;

            }
            connection = ds.getConnection(); //acquire datasource object
            //get user info
            String query = "SELECT * FROM users WHERE username='" + username + "'";
            statement = connection.createStatement();
            result = statement.executeQuery(query);
            if (result.next()) {
                //this user exists
                info[0] = result.getString(1);
                info[1] = result.getString(2);
                info[2] = result.getString(3);
                info[3] = result.getString(4);
                info[4] = result.getString(5);
                info[5] = result.getString(6);
            } else {
                System.out.println("ERROR: Failed to locate user with username = " + username + ".");
                return null;
            }
            if (result.next()) {
                System.out.println("ERROR: Found duplicate users with same username = " + username + ".");
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if(result != null) result.close();
                if(statement != null) statement.close();
                if(connection != null) connection.close();
                return info;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
