import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import javax.sql.DataSource;
import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import javax.naming.*;
/**
 * Created by williamjohnston on 9/22/17.
*/
public class DataSourceFactory {

    public static MysqlConnectionPoolDataSource getDataSource() {
	    Properties properties = new Properties();
	    MysqlConnectionPoolDataSource ds = null; 
	    FileInputStream fis = null;
	    try { 
		ds = new MysqlConnectionPoolDataSource();
		fis = new FileInputStream("/home/will/db.properties");
		properties.load(fis);
		
		ds.setServerName(properties.getProperty("DB_URL")); 
		ds.setUser(properties.getProperty("DB_USERNAME"));
		ds.setPassword(properties.getProperty("DB_PASSWORD"));
		ds.setPortNumber(Integer.parseInt(properties.getProperty("DB_PORT")));
		ds.setDatabaseName(properties.getProperty("DB_NAME"));
		//Context cntx = new InitialContext(properties);
		//cntx.bind("jdbc/pool/scheduleit", ds);
		
	 }
	 catch(IOException/* | NamingException*/ e) {
	 	e.printStackTrace();
	 }
	 return ds;
    }

    public static void main(String[] args) {
	    DataSource ds = getDataSource();
    }
}
