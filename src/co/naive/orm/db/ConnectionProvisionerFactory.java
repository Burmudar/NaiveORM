package co.naive.orm.db;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Creates a ConnectionProvisioner who's responsibility is to get the requestor a valid connection to work with.
 * @author William Bezuidenhout
 *
 */
public final class ConnectionProvisionerFactory {
	
	private static Log logger = LogFactory.getLog(DBFactory.class);
	
	private ConnectionProvisionerFactory() {}
	
	
	public static ConnectionProvisioner initDerbyDB(Map<String,String> metaData) {
		return  new DerbyConnectionProvisioner("", metaData);
	}
	
	
	
}
