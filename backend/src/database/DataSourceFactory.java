import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import javax.sql.DataSource;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
/**
 * Created by williamjohnston on 9/22/17.
*/
public class DataSourceFactory {

    public static DataSource getDataSource() {
	    Properties properties = new Properties();
	    MysqlDataSource ds = null; 
	    FileInputStream fis = null;
	    try { 
		ds = new MysqlDataSource();
		fis = new FileInputStream("/home/will/db.properties");
		properties.load(fis);
		
		ds.setServerName(properties.getProperty("DB_URL")); 
		ds.setUser(properties.getProperty("DB_USERNAME"));
		ds.setPassword(properties.getProperty("DB_PASSWORD"));
		ds.setPortNumber(Integer.parseInt(properties.getProperty("DB_PORT")));
		ds.setDatabaseName(properties.getProperty("DB_NAME"));
	 }
	 catch(IOException e) {
	 	e.printStackTrace();
	 }
	 return ds;
    }
}
