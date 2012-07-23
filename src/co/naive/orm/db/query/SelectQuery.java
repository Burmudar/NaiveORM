package co.naive.orm.db.query;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import co.naive.orm.db.QueryResultTransformer;
import co.naive.orm.db.exception.DatabaseManagerException;

public class SelectQuery<E> extends BaseQuery<E> {

	public SelectQuery(String query, Class<?> resultClass) {
		super(query, resultClass);
	}
	
	public SelectQuery(String query, Class<?> resultClass,
			QueryParameterAdapter queryParameterAdapter) {
		super(query, resultClass, queryParameterAdapter);
	}


	@Override
	public List<E> toResult(ResultSet resultSet) throws DatabaseManagerException {
		QueryResultTransformer<E> mapper = getQueryResultMapper();
		try {
			return mapper.toResultList(resultSet);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseManagerException("<query> Failed to map the result set to an instance of " + getQueryResultClass() != null ? getQueryResultClass().getName() : " null.");
		}
	}

}
