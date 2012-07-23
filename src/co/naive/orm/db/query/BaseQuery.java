package co.naive.orm.db.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import co.naive.orm.db.QueryResultToObject;
import co.naive.orm.db.QueryResultTransformer;
import co.naive.orm.db.exception.DatabaseManagerException;

public abstract class BaseQuery<E> {
	private String queryString;
	private Class<?> queryResultClass;
	private QueryResultTransformer<E> queryResultMapper;
	private QueryParameterAdapter queryParameterAdapter;
	
	public String getQueryString() {
		return queryString;
	}
	
	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}
	
	public Class<?> getQueryResultClass() {
		return queryResultClass;
	}
	
	public void setQueryResultClass(Class<?> queryResultClass) {
		this.queryResultClass = queryResultClass;
	}
	
	public QueryResultTransformer<E> getQueryResultMapper() {
		return queryResultMapper;
	}
	
	public void setQueryResultMapper(QueryResultTransformer<E> queryResultMapper) {
		this.queryResultMapper = queryResultMapper;
	}
	
	public QueryParameterAdapter getQueryParameterAdapter() {
		return queryParameterAdapter;
	}

	public void setQueryParameterAdapter(QueryParameterAdapter queryParameterAdapter) {
		this.queryParameterAdapter = queryParameterAdapter;
	}

	public BaseQuery(String query, Class<?> resultClass) {
		setQueryString(query);
		setQueryResultClass(resultClass);
		setQueryResultMapper(new QueryResultToObject<E>(getQueryResultClass()));
	}
	
	public BaseQuery(String query, Class<?> resultClass, QueryParameterAdapter queryParameterAdapter) {
		setQueryString(query);
		setQueryResultClass(resultClass);
		setQueryResultMapper(new QueryResultToObject<E>(getQueryResultClass()));
		setQueryParameterAdapter(queryParameterAdapter);
	}
	
	public BaseQuery(String queryString,
			QueryResultTransformer<E> queryResultMapper,
			QueryParameterAdapter queryParameterAdapter) {
		super();
		this.queryString = queryString;
		this.queryResultMapper = queryResultMapper;
		this.queryParameterAdapter = queryParameterAdapter;
	}

	public BaseQuery(String queryString,
			QueryParameterAdapter queryParameterAdapter) {
		super();
		this.queryString = queryString;
		this.queryParameterAdapter = queryParameterAdapter;
	}

	public boolean hasQueryParameterAdapter() {
		return getQueryParameterAdapter() != null;
	}
	
	protected void setParametersOn(PreparedStatement preparedStmnt) throws SQLException {
		if(hasQueryParameterAdapter()) {
			getQueryParameterAdapter().setParametersOn(preparedStmnt);
		}
	}
	
	public PreparedStatement toPreparedStatement(Connection connection) throws SQLException {
		PreparedStatement preparedStatement = connection.prepareStatement(getQueryString());
		setParametersOn(preparedStatement);
		return preparedStatement;
	}
	
	public abstract List<E> toResult(ResultSet resultSet) throws DatabaseManagerException;
	
}
