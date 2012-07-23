package co.naive.orm.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import co.naive.orm.db.exception.DatabaseManagerException;
import co.naive.orm.db.query.BaseQuery;
import co.naive.orm.db.query.UpdateQuery;

public class DatabaseManager {
	
	private static Log logger = LogFactory.getLog(DatabaseManager.class);
	private DataSource datasource;
	
	public DataSource getDbSource() {
		return datasource;
	}

	public boolean hasDataSource() {
		return getDbSource() != null;
	}

	public DatabaseManager(DataSource source) {
		this.datasource = source;
	}
	
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
	
	
	public synchronized <E> List<E> executeQuery(BaseQuery<E> query) throws DatabaseManagerException {
		PreparedStatement queryStatement = null;
		ResultSet resultSet = null;
		Connection connection = null;
		try {
			connection = getConnection();
			if(isConnectionValid(connection) == false)
				return new LinkedList<E>();
			logger.debug("<execute> Query [" + query.getQueryString() + "]");
			logger.debug("<execute> Setting parameters on query ...");
			queryStatement = query.toPreparedStatement(connection);
			logger.debug("<execute> Parameters on query was set successfully.");
			boolean executeResult = queryStatement.execute();
			logger.info("<execute> Query executed successfully and exited with result <" + executeResult + ">");
			resultSet = queryStatement.getResultSet();
			return query.toResult(resultSet);
		} catch (SQLException e) {
			rollbackConnection(connection);
			e.printStackTrace();
			throw new DatabaseManagerException("<execute> Failed to execute select query as the database threw an exception: \n" + e.getMessage());
		} finally {
			closeResource(resultSet);
			closeResource(queryStatement);
			closeResource(connection);
		}
	}
	
	public synchronized <E> int executeQuery(UpdateQuery query) throws DatabaseManagerException {
		PreparedStatement queryStatement = null;
		ResultSet resultSet = null;
		Connection connection = null;
		try {
			connection = getConnection();
			if(isConnectionValid(connection) == false)
				return 0;
			logger.debug("<execute> Update Query [" + query.getQueryString() + "]");
			logger.debug("<execute> Setting parameters on query ...");
			queryStatement = query.toPreparedStatement(connection);
			int count = queryStatement.executeUpdate();
			logger.info("<execute> Update Query executed successfully. <" + count + "> row(s) were affected.");
			connection.commit();
			logger.debug("<execute> Query result commited.");
			return count;
		} catch (SQLException e) {
			rollbackConnection(connection);
			e.printStackTrace();
			throw new DatabaseManagerException("<execute> Failed to execute query as the database threw an exception: \n" + e.getMessage());
		} finally {
			closeResource(resultSet);
			closeResource(queryStatement);
			closeResource(connection);
		}
	}
	
	public synchronized <E> int executeQuery(List<UpdateQuery> queries) throws DatabaseManagerException {
		PreparedStatement queryStatement = null;
		ResultSet resultSet = null;
		Connection connection = null;
		int count = 0;
		try {
			connection = getConnection();
			if(isConnectionValid(connection) == false) {
				return 0;
			}
			for(UpdateQuery query : queries) {
				queryStatement = query.toPreparedStatement(connection);
				count += queryStatement.executeUpdate();
			}
			connection.commit();
			return count;
		} catch (SQLException e) {
			rollbackConnection(connection);
			e.printStackTrace();
			throw new DatabaseManagerException("<execute> Failed to execute one or more update queries in the given query list, as the database threw an exception: \n" + e.getMessage());
		} finally {
			closeResource(resultSet);
			closeResource(queryStatement);
			closeResource(connection);
		}
	}
	
	private boolean isConnectionValid(Connection connection) {
		if(connection == null) {
			logger.error("<databaseMngr> Connection received from datasource is null");
			return false;
		}
		try {
			if(connection.isClosed()) {
				logger.error("<databaseMngr> Connection is already closed");
				return false;
			}
		} catch (SQLException e) {
			logger.error("<databaseMngr> Error occured while querying if the connection is closed. Exception: \n" + e.getMessage());
			return false;
		}
		return true;
	}
	
	private void rollbackConnection(Connection connection) throws DatabaseManagerException {
		try {
			connection.rollback();
		} catch (SQLException e1) {
			throw new DatabaseManagerException("<connection> Failed to rollback the connection.");
		}
	}
	
	private void closeResource(ResultSet resultSet) {
		
		try {
			if(resultSet != null && resultSet.isClosed() == false) {
				resultSet.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void closeResource(PreparedStatement preparedStatement) {
		
		try {
			if(preparedStatement != null && preparedStatement.isClosed() == false) {
				preparedStatement.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void closeResource(Connection connection) {
		try {
			if(connection != null && connection.isClosed() == false) {
				connection.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
}
