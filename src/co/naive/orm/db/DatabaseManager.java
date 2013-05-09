package co.naive.orm.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import co.naive.orm.db.exception.DatabaseManagerException;
import co.naive.orm.db.exception.ResultMapException;
import co.naive.orm.db.query.BaseQuery;
import co.naive.orm.db.query.SelectQuery;
import co.naive.orm.db.query.UpdateQuery;

public class DatabaseManager {
	
	private static Log logger = LogFactory.getLog(DatabaseManager.class);
	private ConnectionProvisioner connectionProvisioner;

	public DatabaseManager(ConnectionProvisioner provisioner) {
		this.connectionProvisioner = provisioner;
	}
	
	public <E> List<E> executeQuery(ConnectionInfo connInfo, SelectQuery<E> query) throws DatabaseManagerException {
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet resultSet = null;

		try {
			conn = connInfo == null ? connectionProvisioner.getConnection() : connInfo.getConn();
			logger.debug("<execute> Query [" + query.getQueryString() + "]");
			logger.debug("<execute> Setting parameters on query ...");
			stmt = query.toPreparedStatement(conn);
			logger.debug("<execute> Parameters on query was set successfully.");
			boolean executeResult = stmt.execute();
			logger.info("<execute> Query executed successfully and exited with result <" + executeResult + ">");
			resultSet = stmt.getResultSet();
			logger.info("<execute> Mapping results to list of objects");
			List<E> resultList = query.toResultList(resultSet);
			logger.info("<execute> Post processing result set");
			query.postProcess(resultSet);
			return resultList;
		} catch (SQLException e) {
			throw new DatabaseManagerException("Failed to do executeQuery, sql : " + query == null ? "null" : query.getQueryString(), e);
		} catch (ResultMapException e) {
			throw new DatabaseManagerException("Failed to do executeQuery as the result could not be mapped.", e);
		} finally  {
			closeResources(connInfo == null ? conn : null, stmt, resultSet);
		}
		
	}
	
	public <E> List<E> executeQuery(SelectQuery<E> query) throws DatabaseManagerException {
		return executeQuery(null,query);
	}
	
	public <E> int executeQuery(ConnectionInfo connInfo,UpdateQuery<E> query) throws DatabaseManagerException {
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet resultSet = null;

		try {
			conn = connInfo == null ? connectionProvisioner.getConnection() : connInfo.getConn();
			logger.debug("<execute> Query [" + query.getQueryString() + "]");
			logger.debug("<execute> Setting parameters on query ...");
			stmt = query.toPreparedStatement(conn);
			logger.debug("<execute> Parameters on query was set successfully.");
			int executeResult = stmt.executeUpdate();
			logger.info("<execute> Query executed successfully and exited with result <" + executeResult + ">");
			resultSet = stmt.getGeneratedKeys();
			logger.info("<execute> Mapping generated fields to instances in query");
			query.mapGeneratedFields(resultSet);
			logger.info("<execute> Post processing result set");
			query.postProcess(resultSet);
			return executeResult;
		} catch (SQLException e) {
			throw new DatabaseManagerException("Failed to do executeQuery, sql : " + query == null ? "null" : query.getQueryString(), e);
		} catch (ResultMapException e) {
			throw new DatabaseManagerException("Failed to map Generated Fields after executing sql :" + query == null ? "null" : query.getQueryString(), e);
		} finally  {
			closeResources(connInfo == null ? conn : null, stmt, resultSet);
		}
		
	}

	public <E> int executeQuery(UpdateQuery<E> query) throws DatabaseManagerException {
		return executeQuery(null,query);
	}
	
	
	/**
	 * This method will create a Map of key value pairs where the key contains the key from the mapped DTO which 
	 * was obtained from the {@link KeyDTO#getKey()} method of the dto.
	 * @param sql String value containing the query.
	 * @param type Class object holding the type of object to be mapped.
	 * @param params Object[] of query parameters which should be in the correct order as defined in the sql statement.
	 * @param sorted Indicates if a TreeMap or HashMap will be returned.
	 * @return Map<K,V> of values which can be obtained from the key.
	 * @throws ServiceFailureException
	 */
	public <K, T>
	Map<K,T> executeQueryForKeyMappedList(SelectQuery<T> query, boolean sorted) throws DatabaseManagerException {
		Connection conn = null;
		ResultSet resultSet = null;
		PreparedStatement stmt = null;
		try {
			conn = connectionProvisioner.getConnection();
			stmt = query.toPreparedStatement(conn);
			logger.debug("<execute> Query [" + query.getQueryString() + "]");
			logger.debug("<execute> Setting parameters on query ...");
			stmt = query.toPreparedStatement(conn);
			logger.debug("<execute> Parameters on query was set successfully.");
			boolean executeResult = stmt.execute();
			logger.info("<execute> Query executed successfully and exited with result <" + executeResult + ">");
			resultSet = stmt.getResultSet();
			Map<K, T> resultKeyMap = query.<K>toResultMap(resultSet,sorted);
			logger.info("<execute> Post processing result set");
			query.postProcess(resultSet);
			return resultKeyMap;
		} catch (SQLException e) {
			throw new DatabaseManagerException("Error in queryKeyMappedList.", e);
		} catch (ResultMapException e) {
			throw new DatabaseManagerException("Failed to create Map as there was an error when mapping from the resultSet",e);
		} finally {
			closeResources(conn, stmt, resultSet);
		}
	}
	
	/**
	 * Close resources. If null is passed for a resource, the resource will be ignored.
	 * @param conn Connection
	 * @param stmt Statement
	 * @param rs ResultSet
	 */
	public static void closeResources(Connection conn, Statement stmt, ResultSet rs) {
		closeConnection(conn);
		closeStatement(stmt);
		closeResult(rs);
	}

	/**
	 * Close a result set
	 * @param stmt Statement
	 */
	private static void closeResult(ResultSet rs) {
		if( rs != null ) {
			try {
				if( !rs.isClosed() ) {
					rs.close();
				}
			} catch (SQLException e) {
				logger.error("Error closing the result set.", e);
			}
		}		
	}
	
	/**
	 * Close a connection
	 * @param stmt Statement
	 */
	private static void closeConnection(Connection conn) {
		if( conn != null ) {
			try {
				if( !conn.isClosed() ) {
					conn.close();
				}
			} catch (SQLException e) {
				logger.error("Error closing the connection.", e);
			}
		}
	}

	/**
	 * Close a statment
	 * @param stmt Statement
	 */
	private static void closeStatement(Statement stmt) {
		if( stmt != null ) {
			try {
				if( !stmt.isClosed() ) {
					stmt.close();
				}
			} catch (SQLException e) {
				logger.error("Error closing the statement.", e);
			}
		}
	}


}
