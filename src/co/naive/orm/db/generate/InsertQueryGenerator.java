package co.naive.orm.db.generate;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import co.naive.orm.db.query.UpdateQuery;
import co.naive.orm.refl.ClassInspect;


public class InsertQueryGenerator<E> {
	private static final String INSERT_PREFIX = "INSERT INTO ";
	private static Log logger = LogFactory.getLog(InsertParameterAdapter.class);
	private ClassInspect classInspector;
	private Class<?> instanceClass;
	
	public ClassInspect getClassInspector() {
		return classInspector;
	}

	public void setClassInspector(ClassInspect classInspector) {
		this.classInspector = classInspector;
	}

	public Class<?> getInstanceClass() {
		return instanceClass;
	}

	public void setInstanceClass(Class<?> instanceClass) {
		this.instanceClass = instanceClass;
	}

	public InsertQueryGenerator(Class<?> clazz) {
		this.classInspector = new ClassInspect(clazz);
		this.instanceClass = clazz;
	}
	
	public UpdateQuery<E> generate(E value) {
		logger.info("Generating query [start]");
		String insertQuery = createInsertQuery();
		logger.debug("Class for Insert query generation: " + getInstanceClass().getName());
		logger.debug("Insert query <" + insertQuery + ">");
		List<Entry<Integer,Object[]>> queryParameters = createQueryParametersList(value);
		logger.debug("Generated query has <" + queryParameters.size() + "> parameters");
		InsertParameterAdapter parameterAdapter = new InsertParameterAdapter(queryParameters);
		logger.info("Generating query [done]");
		return new UpdateQuery<E>(insertQuery, parameterAdapter);
	}
	
	public UpdateQuery<E> generate(List<E> valueList) {
		//String insertPrefix = createInsertQuery();
		
		return new UpdateQuery<E>(null);
	}

	private String createInsertQuery() {
		StringBuilder builder = new StringBuilder();
		String insertTable = createTableName();
		String columnListString = createColumnListString();
		builder.append(insertTable);
		builder.append(columnListString);
		return builder.toString();
	}
	
	private String createTableName() {
		ClassInspect inspector = getClassInspector();
		Table tableAnnotation = (Table) inspector.findAnnotation(Table.class);
		if(tableAnnotation == null)
			return "";
		StringBuilder builder = new StringBuilder();
		String schema = tableAnnotation.schema().trim();
		String tableName = tableAnnotation.name();
		builder.delete(0, builder.length());
		builder.append(INSERT_PREFIX);
		builder.append(schema.isEmpty() == false ? schema + "." + tableName : tableName);
		return builder.toString();
	}
	
	private String createColumnListString() {
		ClassInspect inspector = getClassInspector();
		List<Field> annotatedFields = inspector.findAllAnnotatedFields(Column.class);
		int columnCount = 0;
		StringBuilder columnBuilder = new StringBuilder();
		StringBuilder valueBuilder = new StringBuilder();
		columnBuilder.append("(");
		valueBuilder.append("(");
		for(Field field : annotatedFields) {
			if(hasIgnoredAnnotation(field)) {
				continue;
			}
			if(columnCount >= 1) {
				columnBuilder.append(", ");
				valueBuilder.append(", ");
			}
			Column annotation = field.getAnnotation(Column.class);
			if(annotation == null)
				continue;
			columnCount++;
			String columnName = annotation.name();
			columnBuilder.append(columnName);
			valueBuilder.append("?");
		}
		columnBuilder.append(")");
		valueBuilder.append(")");
		return columnBuilder.toString() + " VALUES " + valueBuilder.toString();
	}
	
	private boolean hasIgnoredAnnotation(Field field) {
		return field.getAnnotation(GeneratedValue.class) == null ? false : true;
	}
	
	private List<Entry<Integer, Object[]>> createQueryParametersList(E value) {
		ClassInspect inspector = getClassInspector();
		List<Entry<Integer,Object[]>> queryParameterValues = new LinkedList<Entry<Integer,Object[]>>(); 
		List<Field> annotatedFields = inspector.findAllAnnotatedFields(Column.class);
		int columnCount = 0;
		for(Field field : annotatedFields) {
			if(hasIgnoredAnnotation(field)) {
				logger.debug("Field <" + field.getName() + "> has @Generated annotation. Ignoring for Insert Query");
				continue;
			}
			Column annotation = field.getAnnotation(Column.class);
			if(annotation == null) {
				logger.debug("Field <" + field.getName() + "> has no @Column annotation. Ignoring for Insert Query");
				continue;
			}
			columnCount++;
			Method getMethod = inspector.findGetMethod(field);
			Object getValue = inspector.executeGetMethod(value, getMethod);
			Entry<Integer,Object[]> entry = new SimpleEntry<Integer,Object[]>(columnCount, new Object[] {field,getValue});
			queryParameterValues.add(entry);
		}
		return queryParameterValues;
	}
	
	private class SimpleEntry<K,V> implements Map.Entry<K, V> {

		private K key;
		private V value;
		
		public SimpleEntry(K key, V value) {
			super();
			this.key = key;
			this.value = value;
		}

		@Override
		public K getKey() {
			return key;
		}

		@Override
		public V getValue() {
			return value;
		}

		@Override
		public V setValue(V newValue) {
			V oldValue = value;
			value = newValue;
			return oldValue;
		}
	}
}
