package co.naive.orm.db.query;

import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

import co.naive.orm.db.exception.DatabaseManagerException;

public class UpdateQuery extends BaseQuery<Object> {

	public UpdateQuery(String query, Class<?> resultClass,
			QueryParameterAdapter queryParameterAdapter) {
		super(query, resultClass, queryParameterAdapter);
	}
	
	public UpdateQuery(String query, QueryParameterAdapter queryParameterAdapter) {
		super(query, queryParameterAdapter);
	}
	
	@Override
	public List<Object> toResult(ResultSet resultSet) throws DatabaseManagerException {
		return new LinkedList<Object>();
	}




}
