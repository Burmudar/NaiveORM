package co.naive.orm.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import co.naive.orm.db.exception.ServiceFailureException;


public final class MSSqlDBManager extends DBManagerUtil {
	
	private static Log logger = LogFactory.getLog(MSSqlDBManager.class);
	
	private final String MSSQLConnectionPattern = "jdbc:sqlserver://%s:%s;databaseName=%s"; // host, port and dbname
	private final String host,port,dbname;
	private final String username, password;
	
	static {
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		} catch (ClassNotFoundException e) {
			throw new ExceptionInInitializerError("Failed to load the database driver. " + e.getMessage());
		}
	}
	
	public MSSqlDBManager(Map<String,String> metadata) {
		super(null, metadata);
		host     = metadata.get("host");
		port     = metadata.get("port");
		dbname   = metadata.get("dbname");
		if( StringUtils.isEmpty(host) || StringUtils.isEmpty(port) || StringUtils.isEmpty(dbname)) {
			throw new ExceptionInInitializerError("Invalid metadata. Required information includes, host, port and dbname!");
		}
		username = metadata.get("username");
		password = metadata.get("password");
		resourceName = String.format(MSSQLConnectionPattern, host, port, dbname);
		logger.debug("Configured MSSQL Connection String:\n" + resourceName);
	}
	
	/**
	 * Get a connection from the pool for the specified resource.
	 * 
	 * @see ResourceConstants
	 * 
	 * @param jdbcResourceName String value containing the database resource name.
	 * @return
	 * @throws ServiceFailureException
	 */
	public Connection getConnection() throws ServiceFailureException {
		Connection conn = null;
		try {
			if( logger.isDebugEnabled() ) { logger.debug("Obtaining connection for " + resourceName); }
			conn = DriverManager.getConnection(resourceName, username, password);
		} catch (SQLException e) {
			throw new ServiceFailureException("Failed to obtain a connection for resource: " + resourceName, e);
		}
		return conn;
	}
}
