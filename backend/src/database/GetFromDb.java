package database;
/*
Created by Ryan
 */
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

//TODO Merge this with RetrieveUserInfo
public class GetFromDb {
    //return {id, username, password, name, email, phone}
    public static String[] getUserFromName(String username) {
        MysqlConnectionPoolDataSource ds = null;  //mysql schedule database
        ds = DataSourceFactory.getDataSource();
        if (ds == null) {
            System.out.println("null data source getUserFromName");
            return null;
        }
        Connection connection = null;  //used to connect to database
        Statement statement = null;  //statement to enter command
        ResultSet result = null;  //output after query
        String[] results = new String[6];
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
            //System.out.println("Checking for null");
            /*if (result == null) {
                return null;
            }*/
            result.first();


            results[0] = result.getString("id");
            results[1] = result.getString("username");
            results[2] = result.getString("password");
            results[3] = result.getString("fullname");
            results[4] = result.getString("email");
            results[5] = result.getString("phone_number");

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if(result != null) result.close();
                if(statement != null) statement.close();
                if(connection != null) connection.close();

            } catch (SQLException r) {
                r.printStackTrace();
            }
            return null;
        }
            try {
                if(result != null) result.close();
                if(statement != null) statement.close();
                if(connection != null) connection.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        return results;
    }
}
