package co.naive.orm.db.query;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import co.naive.orm.db.QueryResultTransformer;
import co.naive.orm.db.exception.ResultMapException;



public class SelectQuery<E> extends BaseQuery<E> implements ResultProducer<E> {

	public SelectQuery(String query, Class<?> resultClass) {
		super(query, resultClass);
	}
	
	public SelectQuery(String query, Class<?> resultClass,
			PreQueryAdapter queryParameterAdapter, PostQueryAdapter postQueryAdapter) {
		super(query, resultClass, queryParameterAdapter, postQueryAdapter);
	}
	
	public SelectQuery(String query, Class<?> resultClass, PreQueryAdapter queryParameterAdapter) {
		super(query, resultClass, queryParameterAdapter, null);
	}
	
	public SelectQuery(String query, Class<?> resultClass, PostQueryAdapter postQueryAdapter) {
		super(query, resultClass, null, postQueryAdapter);
	}


	@Override
	public List<E> toResultList(ResultSet resultSet) throws ResultMapException {
		QueryResultTransformer<E> mapper = getQueryResultMapper();
		return mapper.toResultList(resultSet);
	}

	@Override
	public E toResult(ResultSet resultSet) throws ResultMapException {
		QueryResultTransformer<E> mapper = getQueryResultMapper();
		return mapper.toResult(resultSet);
	}

	@Override
	public <K extends Object> Map<K, E> toResultMap(ResultSet resultSet,boolean sorted)
			throws ResultMapException {
		QueryResultTransformer<E> mapper = getQueryResultMapper();
		return mapper.<K>toResultMap(resultSet,sorted);
	}
	
	

}
