package database;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by williamjohnston on 11/20/17.
 */
public class getUsernameFromIdList {

    public static ArrayList<String> getUsernames(ArrayList<Integer> ids) {
        MysqlConnectionPoolDataSource ds = null;  //datasource to connect to database

        Connection connection = null;
        Statement statement = null;
        ResultSet result = null;
        ArrayList<String> admins = null;
        try {
            //call the DataSourceFactory class to create a pooled datasource
            ds = DataSourceFactory.getDataSource();
            //check for potential failed connection
            if (ds != null && ids != null) {

                connection = ds.getConnection(); //acquire datasource object
                admins = getUsernames(ids, connection);  //get usernames for admins
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (result != null) result.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException r) {
                r.printStackTrace();
            }
            return admins;

        }
        try {
            if (result != null) result.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return admins;
    }

    public static ArrayList<String> getUsernames(ArrayList<Integer> ids, Connection connection) {
        int size = ids.size();
        Statement statement = null;
        ResultSet result = null;
        ArrayList<String> usernames = new ArrayList<>(1);

        //find all users, add to string array
        for (int i = 0; i < size; i++) {
            try {
                String query = "SELECT username FROM users WHERE id='" + ids.get(i) + "'";
                statement = connection.createStatement();
                result = statement.executeQuery(query);
                if (result.next()) {
                   usernames.add(result.getString(1));
                } else {
                    return null;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(usernames);
        return usernames;
    }


}
