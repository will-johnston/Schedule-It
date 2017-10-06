package database;
import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

/**
 * Created by williamjohnston on 10/4/17.
 */

public class RetrieveGroupInfo {


    public static int getGroupID(String groupName, int creatorID) {
        MysqlConnectionPoolDataSource ds = null;  //datasource to connect to database
        Connection connection = null;
        Statement statement = null;
        ResultSet result = null;
        int groupID = -1;
        try {
            //call the DataSourceFactory class to create a pooled datasource
            ds = DataSourceFactory.getDataSource();
            //check for potential failed connection
            boolean flag = true;  //catches datasource errors
            if (ds == null) {
                System.out.println("ERROR: could not get datasource object");
                flag = false;
            }
            connection = ds.getConnection(); //acquire datasource object
            if (connection == null) {
                System.out.println("ERROR: could not connect to the datasource");
                flag = false;
            }
            if (flag) {
                //find group
                String query = "SELECT id FROM groups WHERE name='" + groupName + "' AND creatorID=" + creatorID;

                statement = connection.createStatement();

                result = statement.executeQuery(query);
                //query the database
                if (result.isBeforeFirst()) {
                    //this group exists
                    result.next();
                    groupID = result.getInt(1);
                    //sanity check
                    if (result.next()) {
                        System.out.println("ERROR: found multiple groups with same name and creator.");
                        groupID = -1;
                    }
                }
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
            return groupID;
        }
        try {
            if (result != null) result.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();
            return groupID;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groupID;
    }

    public static String getGroupName(int groupID) {
        MysqlConnectionPoolDataSource ds = null;  //datasource to connect to database
        Connection connection = null;
        Statement statement = null;
        ResultSet result = null;
        String groupName = null;
        try {
            //call the DataSourceFactory class to create a pooled datasource
            ds = DataSourceFactory.getDataSource();
            //check for potential failed connection
            if (ds == null) {
                System.out.println("ERROR: could not get datasource object");
                return null;
            }

            connection = ds.getConnection(); //acquire datasource object
            if (connection == null) {
                System.out.println("ERROR: could not connect to the datasource");
                return null;
            }

            //find groupName
            String query = "SELECT name FROM groups WHERE id=" + groupID;

            statement = connection.createStatement();

            result = statement.executeQuery(query);
            //query the database
            if (result.isBeforeFirst()) {
                //this group exists
                result.next();
                groupName = result.getString(1);
                //sanity check
                if (result.next()) {
                    System.out.println("ERROR: found multiple groups with same id.");
                    return null;
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if(result != null) result.close();
                if(statement != null) statement.close();
                if(connection != null) connection.close();
                return groupName;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
    public static int getGroupCreatorID(int groupID) {
        MysqlConnectionPoolDataSource ds = null;  //datasource to connect to database
        Connection connection = null;
        Statement statement = null;
        ResultSet result = null;
        int userID = -1;
        try {
            //call the DataSourceFactory class to create a pooled datasource
            ds = DataSourceFactory.getDataSource();
            //check for potential failed connection
            if (ds == null) {
                System.out.println("ERROR: could not get datasource object");
                return -1;

            }
            connection = ds.getConnection(); //acquire datasource object
            if (connection == null) {
                System.out.println("ERROR: could not connect to the datasource");
                return -1;
            }

            //find groupName
            String query = "SELECT creatorID FROM groups WHERE id=" + groupID;
            statement = connection.createStatement();
            result = statement.executeQuery(query);
            //query the database
            if (result.isBeforeFirst()) {
                //this group exists
                result.next();
                userID = result.getInt(1);
                //sanity check
                if (result.next()) {
                    System.out.println("ERROR: found multiple groups with same id.");
                    return -1;
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        } finally {
            try {
                if(result != null) result.close();
                if(statement != null) statement.close();
                if(connection != null) connection.close();
                return userID;
            } catch (SQLException e) {
                e.printStackTrace();
                return -1;
            }
        }
    }

    public static LinkedList<Integer> getGroupUserIDs(int groupID) {
        MysqlConnectionPoolDataSource ds = null;  //datasource to connect to database

        Connection connection = null;
        Statement statement = null;
        ResultSet result = null;
        LinkedList<Integer> list = new LinkedList<>();
        int ids[] = null;
        try {
            //call the DataSourceFactory class to create a pooled datasource
            ds = DataSourceFactory.getDataSource();
            //check for potential failed connection
            if (ds == null) {
                return null;

            }
            connection = ds.getConnection(); //acquire datasource object


            //query junction table
            String query = "SELECT userID FROM group_user_junction WHERE groupId=" + groupID;
            statement = connection.createStatement();
            //send an add user query to the database
            result = statement.executeQuery(query);
            if (result.isBeforeFirst()) {
                //this group has users
                while (result.next()) {
                    list.addFirst(result.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if(result != null) result.close();
                if(statement != null) statement.close();
                if(connection != null) connection.close();
                return list;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}

