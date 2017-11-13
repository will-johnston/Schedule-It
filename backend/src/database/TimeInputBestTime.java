package database;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
        ResultSet rs = null;
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

            List<Long> times = new ArrayList();
            //get all times, convert to integers
            //SELECT UNIX_TIMESTAMP(yourfield) FROM yourtable;
            rs = statement.executeQuery("select UNIX_TIMESTAMP(time_preference) from time_inputs where groupID=" + groupID + " and eventID=" + eventID);
            while (rs.next()) {
                long input = rs.getLong(1);
                times.add(input);
                System.out.println(input);
            }
            long best;
            if (times.size() < 1) {
                //no time inputs, make time tomorrow at 6:00 pm
                String date = java.time.LocalDate.now().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Calendar c = Calendar.getInstance();
                c.setTime(sdf.parse(date));
                c.add(Calendar.DATE, 1);  // number of days to add
                best = c.getTimeInMillis();
            } else {
                Scheduler s = new Scheduler(times);
                best = s.findBestTime();
            }

            //convert back to datetime
            //select datetime( 1323648000, 'unixepoch' );
            //SELECT FROM_UNIXTIME(1111885200);

            //add "time" to event table

            //change "is_polling_users" field to negative

            //clear all time inputs for group in database

        } catch (SQLException e) {
            e.printStackTrace();
            ret = false;
            try {
                if (rs != null) rs.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException etwo) {
                etwo.printStackTrace();
                ret = false;
            }
            return ret;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            if (rs != null) rs.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
            ret = false;
        }
        return ret;
    }





}
