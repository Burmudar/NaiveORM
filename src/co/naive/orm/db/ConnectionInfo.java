package co.naive.orm.db;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import co.naive.orm.db.exception.DatabaseManagerException;
import co.naive.orm.db.exception.ServiceFailureException;


/**
 * Use this class to control the connection and transactions between SQL calls where more than one call
 * is made to tables and transaction funcionality is required.
 * 
 * @author Joe Potgieter (joe@bbd.co.za)
 *
 */
public class ConnectionInfo {
	private static Log logger = LogFactory.getLog(ConnectionInfo.class);

	private Connection conn;
	boolean inTransaction = false;
	boolean originalCommit;
	
	public Connection getConn() {
		return conn;
	}

	public boolean isInTransaction() {
		return inTransaction;
	}

	public boolean isOriginalCommit() {
		return originalCommit;
	}

	public ConnectionInfo(Connection connection) throws DatabaseManagerException {
		conn = connection;
	}

	/*
	protected Connection getManagedConnection() {
		return conn;
	}
	*/
	
	@Override
	protected void finalize() throws Throwable {
		// Safety gateway for developers forgetting to close their managed connections.
		if( closeResources() ) {
			logger.warn("NOTE! DBManagerUtil detected that a resource has not closed the connections that it started! Automatic closure in DBManagerUtil freed the resources up!");
			Thread.dumpStack();
		}
		super.finalize();
	}
	
	protected boolean closeResources() {
		try {
			if( conn != null && !conn.isClosed() ) {
				// This will roll the transaction back if there was a transaction active.
				if( inTransaction && conn.getAutoCommit() == false ) {
					logger.warn("Automatically rolling back the transaction.");
					conn.rollback();
				}
				conn.setAutoCommit(originalCommit);
				conn.close();
				return true;
			} else {
				return false;
			}
		} catch(Exception e) {
			logger.error("Close connection failed!",e);
			return false;
		}
	}

	public boolean startTransaction() throws DatabaseManagerException {
		if( !inTransaction ) {
			if( conn == null ) {
				throw new IllegalStateException("Connection closed!");
			} 
			try {
				originalCommit = conn.getAutoCommit();
				conn.setAutoCommit(false);
				inTransaction = true;
			} catch(SQLException e) {
				throw new DatabaseManagerException("Error starting transaction.", e);
			}
		}
		return inTransaction;
	}
	
	public boolean stopTransaction() throws DatabaseManagerException {
		if( inTransaction ) {
			if( conn == null ) {
				throw new IllegalStateException("Connection closed!");
			} 
			try {
				conn.setAutoCommit(originalCommit);
				inTransaction = false;
			} catch(SQLException e) {
				throw new DatabaseManagerException("Error stopping transaction.", e);
			}
		}
		return inTransaction;
	}

	public void commit() throws DatabaseManagerException {
		try {
			if( conn == null || conn.isClosed() || conn.getAutoCommit() == true ) {
				throw new IllegalStateException("Transaction not valid for commit operation.");
			}
			conn.commit();
		} catch(SQLException e) {
			throw new DatabaseManagerException("Error committing the transaction!",e);
		}
	}
	
	public void rollback() throws DatabaseManagerException {
		try {
			if( conn == null || conn.isClosed() || conn.getAutoCommit() == true ) {
				throw new IllegalStateException("Transaction not valid for commit operation.");
			}	
			conn.rollback();
		} catch(SQLException e) {
			throw new DatabaseManagerException("Error rolling back the transaction!");
		}
	}
		
	
}
