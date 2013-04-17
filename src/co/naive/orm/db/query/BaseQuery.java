package co.naive.orm.db.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import co.naive.orm.db.DefaultQueryResultMapper;
import co.naive.orm.db.QueryResultTransformer;


public abstract class BaseQuery<E> {
	private String queryString;
	protected Class<?> queryResultClass;
	protected QueryResultTransformer<E> queryResultMapper;
	protected PreQueryAdapter queryParameterAdapter;
	protected PostQueryAdapter postQueryAdapter;
	
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
	
	public PostQueryAdapter getPostQueryAdapter() {
		return postQueryAdapter;
	}

	public void setPostQueryAdapter(PostQueryAdapter postQueryAdapter) {
		this.postQueryAdapter = postQueryAdapter;
	}

	public PreQueryAdapter getQueryParameterAdapter() {
		return queryParameterAdapter;
	}

	public void setQueryParameterAdapter(PreQueryAdapter queryParameterAdapter) {
		this.queryParameterAdapter = queryParameterAdapter;
	}
	
	

	public BaseQuery(String query, Class<?> resultClass) {
		setQueryString(query);
		setQueryResultClass(resultClass);
		setQueryResultMapper(new DefaultQueryResultMapper<E>(getQueryResultClass()));
	}
	
	public BaseQuery(String query, Class<?> resultClass, PreQueryAdapter queryParameterAdapter, PostQueryAdapter postQueryAdapter) {
		setQueryString(query);
		setQueryResultClass(resultClass);
		setQueryResultMapper(new DefaultQueryResultMapper<E>(getQueryResultClass()));
		setQueryParameterAdapter(queryParameterAdapter);
		setPostQueryAdapter(postQueryAdapter);
	}
	
	public BaseQuery(String queryString,
			QueryResultTransformer<E> queryResultMapper,
			PreQueryAdapter queryParameterAdapter,
			PostQueryAdapter postQueryAdapter) {
		super();
		this.queryString = queryString;
		this.queryResultMapper = queryResultMapper;
		this.queryParameterAdapter = queryParameterAdapter;
		this.postQueryAdapter = postQueryAdapter;
	}

	public BaseQuery(String queryString,
			PreQueryAdapter queryParameterAdapter) {
		super();
		this.queryString = queryString;
		this.queryParameterAdapter = queryParameterAdapter;
	}
	
	public BaseQuery(String queryString,
			PostQueryAdapter postQueryAdapter) {
		super();
		this.queryString = queryString;
		this.postQueryAdapter = postQueryAdapter;
	}
	
	public BaseQuery(String queryString) {
		super();
		this.queryString = queryString;
	}

	public boolean hasQueryParameterAdapter() {
		return getQueryParameterAdapter() != null;
	}
	
	public boolean hasPostQueryAdapter() {
		return postQueryAdapter != null;
	}
	
	protected void setParametersOn(PreparedStatement preparedStmnt) throws SQLException {
		if(hasQueryParameterAdapter()) {
			queryParameterAdapter.setParametersOn(preparedStmnt);
		}
	}
	
	public PreparedStatement toPreparedStatement(Connection connection) throws SQLException {
		PreparedStatement preparedStatement = connection.prepareStatement(getQueryString());
		setParametersOn(preparedStatement);
		return preparedStatement;
	}
	
	public void postProcess(ResultSet resultSet) throws SQLException {
		if(hasPostQueryAdapter()) {
			postQueryAdapter.process(resultSet);
		}
	}

	
}
