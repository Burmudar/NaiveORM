package co.naive.orm.db;

import java.sql.Connection;

import co.naive.orm.db.exception.DatabaseManagerException;

public interface ConnectionProvisioner {
	Connection getConnection() throws DatabaseManagerException;
	ConnectionInfo getManagedConnection() throws DatabaseManagerException;
}
