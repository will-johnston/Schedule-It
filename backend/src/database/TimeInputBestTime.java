package database;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by williamjohnston on 11/8/17.
 */

public class TimeInputBestTime {
    public static boolean findBestTime(int groupID, int eventID) {
        MysqlConnectionPoolDataSource ds = null;  //datasource to connect to database
        Connection connection = null;
        Statement statement = null;
        boolean ret = true;
        try {
            //Call DataSourceFactory
            ds = DataSourceFactory.getDataSource();
            //Check for potential failed connection
            if (ds == null) {
                ret = false;
            }
            //Acquire datasource object
            connection = ds.getConnection();
            List<Long> times = new ArrayList<Long>();
            //get all times, convert to integers
            //SELECT UNIX_TIMESTAMP(yourfield) FROM yourtable;


	    statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select UNIX_TIMESTAMP(time_preference) from time_inputs where groupID=" + groupID + " and eventID=" + eventID);
            long input;
            if(rs != null) {
                while (rs.next()) {
                    input = (long) rs.getInt(1);
                    times.add(input);
                }
                rs.close();
            }

            long best;
            if (times.size() < 1) {
                //no time inputs, make time expiration_date plus one day

                java.sql.Timestamp ts = new java.sql.Timestamp(new java.util.Date().getTime());
                Calendar cal = Calendar.getInstance();
                cal.setTime(ts);
                cal.add(Calendar.DAY_OF_WEEK, 1);
                ts.setTime(cal.getTime().getTime()); // or
                ts = new Timestamp(cal.getTime().getTime());

                String time = ts.toString();
                //System.out.println(time);

                //add "time" to event table, converting back to datetime
                String addPreset = "UPDATE events SET time='" + time + "' where eventID=" + eventID;
                statement = connection.createStatement();
                statement.executeUpdate(addPreset);
            } else {
                Scheduler s = new Scheduler(times);
                best = s.findBestTime();

                //add "time" to event table, converting back to datetime
                String addEvent = "UPDATE events SET time=FROM_UNIXTIME(" + best + ") where eventID=" + eventID;
                statement = connection.createStatement();
                statement.executeUpdate(addEvent);
            }

            //change "is_polling_users" field to negative
            String changeField = "UPDATE events SET is_polling_users=-1 where eventID=" + eventID;
            statement = connection.createStatement();
            statement.executeUpdate(changeField);

            //clear all time inputs for group and event in database
            
            String clearTimeInputs = "DELETE FROM time_inputs WHERE eventID=" + eventID + " AND groupID=" + groupID;
            statement = connection.createStatement();
            statement.executeUpdate(clearTimeInputs);

        } catch (SQLException e) {
            e.printStackTrace();
            ret = false;
            try {
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException etwo) {
                etwo.printStackTrace();
                ret = false;
            }
            return ret;
        }
        try {
            if (statement != null) statement.close();
            if (connection != null) connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
            ret = false;
        }
        return ret;
    }
}
