package co.naive.orm.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

import co.naive.orm.db.exception.ServiceFailureException;


public class DerbyDBManager extends DBManagerUtil {
	private String host,port,dbname,username,password;
	
	protected DerbyDBManager(String resourceName, Map<String, String> metadata) {
		super(resourceName, metadata);
		host = metadata.get("host");
		port = metadata.get("port");
		dbname = metadata.get("dbname");
	}

	@Override
	public Connection getConnection() throws ServiceFailureException {
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
			return DriverManager.getConnection("jdbc:derby:" + createConnectionString());
		} catch (SQLException e) {
			throw new ServiceFailureException("Failed to get connection using url: " + createConnectionString(),e);
		} catch (ClassNotFoundException e) {
			throw new ServiceFailureException("Failed to get connection as the Derby class could not be found.",e);
		}	
	}
	
	private String createConnectionString() {
		StringBuilder builder = new StringBuilder(host);
		builder.append(port);
		builder.append(dbname);
		builder.append(";create=true");
		return builder.toString();
	}

}
