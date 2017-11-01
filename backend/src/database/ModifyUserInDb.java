package database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
		
public class ModifyUserInDb {
	/*public static void main(String[] args) {
		String mods[] = {"email", "example@gmail.com", "fullname", "Clarence tarence", "password", "pss", "phone_number", "7"};
		modifyUser(mods, "CLARENCE");
	}*/
	/*
	 * Update any user information except id 
	 * Strint[] mods (modifications), format: [col1_name, col1_value, col2_name, col2_value ...]
	 * example format: modifications = [email, jon_snow@averagemail.com, password, urabastardjonsnow]
	 */		
	public static boolean modifyUser(String[] mods, String username) {
        //if invalid input
        if (mods.length < 2 || mods.length % 2 != 0 || mods == null || username == null) {
            System.out.println("invalid input: make sure to follow proper mods format"
                    + "and inputs are not null.");
            return false;
        }
        boolean ret = true;

        MysqlConnectionPoolDataSource ds = null;  //mysql schedule database
        ds = DataSourceFactory.getDataSource();
        if (ds == null) {
            System.out.println("null data source");
            return false;
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
                    + " from users where username='" + username + "'";
            System.out.println(query);
            result = statement.executeQuery(query);
            //there is only one possible result due to add user parameters
            result.next();
            //modify result
            int length = mods.length;
            for (int i = 0; i < length; i += 2) {
                if (!mods[i].equals("fullname")
                        && !mods[i].equals("password") && !mods[i].equals("email")
                        && !mods[i].equals("phone_number")) {
                    //invalid input
                    //rollback
                    System.out.println("INVALID INPUT");
                    ret = false;
                    break;
                } else {
                    result.updateString(mods[i], mods[i + 1]);
                }
            }

            result.updateRow();

        } catch (SQLException e) {
            ret = false;
            e.printStackTrace();
            try {
                if (result != null) result.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();

            } catch (SQLException r) {
                r.printStackTrace();
            }
            return ret;
        }
        try {
            if (result != null) result.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }
    public static boolean setNotificationPrefs(String newvalue, int userid) {
        MysqlConnectionPoolDataSource ds = null;  //mysql schedule database
        ds = DataSourceFactory.getDataSource();
        if (ds == null) {
            System.out.println("null data source getFriends");
            return false;
        }
        Connection connection = null;  //used to connect to database
        Statement statement = null;  //statement to enter command
        try {
            //set up connection
            connection = ds.getConnection();
            //create statement
            //statement = connection.createStatement();
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            //query  database
            System.out.println("Formatting query getFriends");
            //select * from group_user_junction where groupID=3;
            //String query = String.format("SELECT * FROM group_user_junction WHERE userID=%d;", userid);
            String query = String.format("UPDATE users SET notif_pref_group=%s WHERE id=%d;", handleNull(newvalue), userid);
            System.out.println(query);
            int result = statement.executeUpdate(query);
            if (result == 1) {
                return true;
            }
            else {
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static String handleNull(String value) {
	    if (value == null || value.equals("NULL")) {
	        return "NULL";
        }
        else {
	        return String.format("'%s'", value);
        }
    }
}

