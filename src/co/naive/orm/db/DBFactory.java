package co.naive.orm.db;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Constructs a DMManagerUtil class
 * @author Joe
 *
 */
public final class DBFactory {
	
	private static Log logger = LogFactory.getLog(DBFactory.class);
	
	private DBFactory() {}
	
	
	public static DBManagerUtil initDerbyDB(Map<String,String> metaData) {
		return  new DerbyDBManager("", metaData);
	}

	public static DBManagerUtil initMSSqlDBManager() throws ExceptionInInitializerError {
		try {
			// Load the properties from a properties file.
			Properties props = new Properties();
			InputStream io = DBFactory.class.getResourceAsStream("mssql.properties");
			props.load(io);
			return new MSSqlDBManager((Map)props);
		} catch (IOException e) {
			logger.error("Unable to load the properties for the local mssql connection.", e);
			throw new ExceptionInInitializerError(e.getMessage());
		}
	}
	
	
	
}
