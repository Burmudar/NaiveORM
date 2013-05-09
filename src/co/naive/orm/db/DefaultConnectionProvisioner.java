package co.naive.orm.db;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import co.naive.orm.db.exception.DatabaseManagerException;

public class DefaultConnectionProvisioner implements ConnectionProvisioner {
	private DataSource datasource;
	
	public DataSource getDbSource() {
		return datasource;
	}

	public boolean hasDataSource() {
		return getDbSource() != null;
	}
	public DefaultConnectionProvisioner(DataSource dataSource) {
		this.datasource = dataSource;
	}
	
	@Override
	public Connection getConnection() throws DatabaseManagerException {
		DataSource dataSource = getDbSource(); 
		if(dataSource == null) {
			throw new DatabaseManagerException("<connection> No Datasource. Cannot get a connection.");
		}
		try {
			return dataSource.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseManagerException(e.getMessage());
		}
	}

	@Override
	public ConnectionInfo getManagedConnection() {
		// TODO Auto-generated method stub
		return null;
	}

}
