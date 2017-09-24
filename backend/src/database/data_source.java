import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import javax.sql.DataSource;
import java.sql.*;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
/**
 * Created by williamjohnston on 9/22/17.
 */
public class data_source {
    public static void main(String[] args) {
        System.out.println("hello");
	 try { 
		MysqlDataSource dataSource = new MysqlDataSource();
		dataSource.setUser("root");
		dataSource.setPassword("");
		dataSource.setServerName("localhost");
		dataSource.setPortNumber(3306);
		dataSource.setDatabaseName("scheduleit");
		Connection conn = dataSource.getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("select * from information_schema.tables");
		rs.next();
		System.out.println(rs.getString(1));
		rs.close();
		stmt.close();
		conn.close();
	 }
	 catch(SQLException e) {
	 	e.printStackTrace();
	 }
	 }
}
