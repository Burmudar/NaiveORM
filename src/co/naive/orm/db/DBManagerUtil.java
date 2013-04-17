package co.naive.orm.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import co.naive.orm.db.exception.ResultMapException;
import co.naive.orm.db.exception.ServiceFailureException;
import co.naive.orm.db.query.SelectQuery;
import co.naive.orm.db.query.UpdateQuery;
import co.naive.orm.general.General;


public abstract class DBManagerUtil {

	private static Log logger = LogFactory.getLog(DBManagerUtil.class);
	
	protected String resourceName; // Either jndi / jdbc connection name.
	
	protected Map<String,String> metadata;
	
	protected DBManagerUtil(String resourceName, Map<String,String> metadata) {
		this.metadata = metadata;
		this.resourceName = resourceName;
	}
	
	public abstract Connection getConnection() throws ServiceFailureException;
	
	public ConnectionInfo getManagedConnection() throws ServiceFailureException {
		return new ConnectionInfo(getConnection());
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

    /**
     * Traverse the SQL exception for detailed information.
     * @param e SQLException
     * @param logger Log to use to log the exception.
     */
    public static void logSQLException(SQLException e, Log logger) {
        StringBuilder builder = new StringBuilder("Unable perform operation due to an SQL Exception : ").append(General.EOL).append("Errors Read:").append(General.EOL).append("------------").append(General.EOL).append(General.EOL);
        builder.append(e.getMessage());
        SQLException t = e.getNextException();
        while( t != null ) {
            builder.append(General.EOL).append(t.getMessage());
            t = t.getNextException();
        }
        logger.error(builder.toString(), e);
    }
    
	public <E> List<E> executeQuery(ConnectionInfo connInfo, SelectQuery<E> query) throws ServiceFailureException {
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet resultSet = null;

		try {
			conn = connInfo == null ? getConnection() : connInfo.getConn();
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
			throw new ServiceFailureException("Failed to do executeQuery, sql : " + query == null ? "null" : query.getQueryString(), e);
		} catch (ResultMapException e) {
			throw new ServiceFailureException("Failed to do executeQuery as the result could not be mapped.", e);
		} finally  {
			DBManagerUtil.closeResources(connInfo == null ? conn : null, stmt, resultSet);
		}
		
	}
	
	public <E> List<E> executeQuery(SelectQuery<E> query) throws ServiceFailureException {
		return executeQuery(null,query);
	}
	
	public <E> int executeQuery(ConnectionInfo connInfo,UpdateQuery<E> query) throws ServiceFailureException {
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet resultSet = null;

		try {
			conn = connInfo == null ? getConnection() : connInfo.getConn();
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
			throw new ServiceFailureException("Failed to do executeQuery, sql : " + query == null ? "null" : query.getQueryString(), e);
		} catch (ResultMapException e) {
			throw new ServiceFailureException("Failed to map Generated Fields after executing sql :" + query == null ? "null" : query.getQueryString(), e);
		} finally  {
			DBManagerUtil.closeResources(connInfo == null ? conn : null, stmt, resultSet);
		}
		
	}

	public <E> int executeQuery(UpdateQuery<E> query) throws ServiceFailureException {
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
	Map<K,T> executeQueryForKeyMappedList(SelectQuery<T> query, boolean sorted) throws ServiceFailureException {
		Connection conn = null;
		ResultSet resultSet = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
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
			throw new ServiceFailureException("Error in queryKeyMappedList.", e);
		} catch (ResultMapException e) {
			throw new ServiceFailureException("Failed to create Map as there was an error when mapping from the resultSet",e);
		} finally {
			DBManagerUtil.closeResources(conn, stmt, resultSet);
		}
	}
	
}
