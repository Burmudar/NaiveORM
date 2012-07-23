package co.naive.orm.db;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;

import co.naive.orm.util.ClassInspect;


public class QueryResultToObject<E> implements QueryResultTransformer<E>{
	private ClassInspect classInspector;
	
	public ClassInspect getClassInspector() {
		return classInspector;
	}

	public void setClassInspector(ClassInspect classInspector) {
		this.classInspector = classInspector;
	}
	
	

	public QueryResultToObject(Class<?> clazz) {
		setClassInspector(new ClassInspect(clazz));
	}

	@SuppressWarnings("unchecked")
	protected E newInstance() {
		try {
			return (E) getClassInspector().getInspectedClass().newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Inspects the given class for fields to map from the ResultSet to an actual instance of the class.
	 * If the given resultSet is null, this method will return an empty list. The method only considers fields
	 * that have been annotated with the Column Annotation.
	 * @param resultSet
	 * @return List that contains instances that have been mapped of the given class.
	 * @throws SQLException
	 */
	@Override
	public List<E> toResultList(ResultSet resultSet) throws SQLException {
		if(resultSet == null) {
			return new LinkedList<E>();
		}
		if(resultSet.isClosed()) {
			return new LinkedList<E>();
		}
		List<Field> annotatedFields = getClassInspector().findAllAnnotatedFields(Column.class);
		List<E> resultList = new LinkedList<E>();
		try {
			while(resultSet.next()) {
				E instance = newInstance();
				for(Field field : annotatedFields) {
					String columnName = field.getAnnotation(Column.class).name();
					Object value = resultSet.getObject(columnName);
					Method setMethod = getClassInspector().findSetMethod(field, field.getType());
					getClassInspector().executeSetMethod(instance,setMethod,value);
				}
				resultList.add(instance);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return resultList;
		
	}
}
