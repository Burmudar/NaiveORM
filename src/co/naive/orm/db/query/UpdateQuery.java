package co.naive.orm.db.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import co.naive.orm.db.exception.ResultMapException;



public class UpdateQuery<E> extends BaseQuery<E> {
	private List<E> instances;
	
	public UpdateQuery(String query, E instance,
			PreQueryAdapter queryParameterAdapter, PostQueryAdapter postQueryAdapter) {
		super(query, instance.getClass(), queryParameterAdapter, postQueryAdapter);
		initialiseListAndAddInstance(instance);
	}
	
	public UpdateQuery(String query, List<E> instances,
			PreQueryAdapter queryParameterAdapter, PostQueryAdapter postQueryAdapter) {
		super(query, instances.get(0).getClass(), queryParameterAdapter, postQueryAdapter);
		this.instances = instances;
	}
	
	public UpdateQuery(String query, PreQueryAdapter queryParameterAdapter) {
		super(query, queryParameterAdapter);
	}
	
	public UpdateQuery(String query, E instance, PostQueryAdapter postQueryAdapter) {
		super(query, instance.getClass(), null,postQueryAdapter);
		initialiseListAndAddInstance(instance);
	}
	
	public UpdateQuery(String query, E instance, PreQueryAdapter queryParameterAdapter) {
		super(query, instance.getClass(),queryParameterAdapter,null);
		initialiseListAndAddInstance(instance);
	}
	
	protected void initialiseListAndAddInstance(E instance) {
		instances = new LinkedList<E>();
		instances.add(instance);
	}
	
	public UpdateQuery(String query, PostQueryAdapter postQueryAdapter) {
		super(query, postQueryAdapter);
	}
	
	public UpdateQuery(String query) {
		super(query);
	}
	
	@Override
	public PreparedStatement toPreparedStatement(Connection connection) throws SQLException {
		PreparedStatement prepareStatement = connection.prepareStatement(getQueryString(), Statement.RETURN_GENERATED_KEYS);
		setParametersOn(prepareStatement);
		return prepareStatement;
	}
	
	public void mapGeneratedFields(ResultSet resultSet) throws ResultMapException  {
		if(instances != null && !instances.isEmpty()) {
			queryResultMapper.mapGenerated(instances, resultSet);
		}
	}

	public List<E> getInstances() {
		return instances;
	}

	public void setInstances(List<E> instances) {
		this.instances = instances;
	}
	
	



}
