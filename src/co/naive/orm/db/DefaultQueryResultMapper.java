package co.naive.orm.db;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.GeneratedValue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import co.naive.orm.annotation.Key;
import co.naive.orm.db.exception.ResultMapException;
import co.naive.orm.refl.ClassInspect;


/**
 * <p>
 * Class is used to map a values in a ResultSet to objects instances that are of type <E>.
 * Values are retrieved from the ResultSet based on column names. Only fields that are annotated
 * with the {@link Column} or {@link Embedded} annotation are considered relevant for mapping.
 * </p>
 * <p>
 * 
 * <b><i>Note</i></b> that the only attribute that is considered and used in the Column annotation is the 
 * name attribute. No attributes in the Embedded annotation are considered.
 * </p>
 * <p>
 * Three methods are provided to provide various result forms, namely: Single instance, List of Instances
 * or a Map of instances.
 * </p>
 * <p>
 * In the case of a map there must be at least one field that is annotated with the {@link Key} annotation. A
 * {@link ResultMapException} is thrown if not field is found to contain that annotation.
 * </p>
 * <p>
 * The method mapGenerated is used when the given ResultSet returns values of auto
 * </p>
 * <p>
 * Finally, the class also contains two forms of mapping. <b>Strict mapping</b> and <b>Lenient mapping</b>. The default
 * setting for this class is to perform strict mapping, in which case a specified column is not found
 * in the resultset a {@link ResultMapException} is thrown. 
 * <p>
 * Lenient mapping does not throw a exception instead it just generates a warning in the log.
 * 
 * @author William Bezuidenhout (bbdnet1030)
 *
 * @param <E> Any class whose fields are annotated with {@link Column} or {@link Embedded} annotations
 */
public class DefaultQueryResultMapper<E> implements QueryResultTransformer<E>{
	private static Log logger = LogFactory.getLog(DBFactory.class);
	private boolean strict;
	
	private ClassInspect classInspector;
	
	public ClassInspect getClassInspector() {
		return classInspector;
	}

	public void setClassInspector(ClassInspect classInspector) {
		this.classInspector = classInspector;
	}

	public DefaultQueryResultMapper(Class<?> clazz) {
		setClassInspector(new ClassInspect(clazz));
		useStrictMapping();
	}
	
	public DefaultQueryResultMapper(Class<?> clazz, boolean strictMapping) {
		setClassInspector(new ClassInspect(clazz));
		strict = strictMapping;
	}
	
	public void useStrictMapping() {
		strict = true;
		
	}

	public void useLenientMapping() {
		strict = false;
	}
	
	
	/**
	 * Same as {@link DefaultQueryResultMapper#mapGenerated(Object, ResultSet)}, just for list of objects
	 */
	@Override
	public void mapGenerated(List<E> instances, ResultSet resultSet) throws ResultMapException {
		for(E instance : instances) {
			try {
				if(resultSet.next()) {
					mapGenerated(instance, resultSet);
				}
			} catch(SQLException e) {
				throw new ResultMapException("Failed to traverse the ResultSet while trying to map Generated fields.",e);
			}
		}
	}

	/**
	 * <p>
	 * Inspects the given class of the provided instance for all fields annotated with @{@link GeneratedValue}. All fields
	 * marked with this annotation are then set with values found in the provided ResultSet. The values are retrieved from
	 * the ResultSet by index and <u>not by column name</u>. Therefore the order of the {@link GeneratedValue} annotation does matter.
	 * </p>
	 * @param resultSet - ResultSet to use when mapping generated fields
	 * @param instance - The instance whose marked fields should be mapped
	 */
	@Override
	public void mapGenerated(E instance,ResultSet resultSet) {
		if(instance == null) {
			logger.warn("Received a NULL instance to map generated. Not doing anything and returning.");
		}
		List<Field> generatedFields = classInspector.findAllAnnotatedFields(GeneratedValue.class);
		for(int i = 0; i < generatedFields.size(); i++) {
			Field field = generatedFields.get(i);
			logger.debug("Setting <" + field.getName() + "> that has @GeneratedValue annotation with value at [" + i + "] of the ResultSet.");
			Method setMethod = classInspector.findSetMethod(generatedFields.get(i), field.getType() );
			try {
				Object object = resultSet.getObject(i + 1);
				if(object instanceof BigDecimal) {
					object = getValueForFieldType((BigDecimal) object, field);
				} else if (object instanceof BigInteger) {
					object = getValueForFieldType((BigInteger) object, field);
				}
				classInspector.executeSetMethod(instance, setMethod, object);
			} catch (SQLException e) {
				logger.warn("Got an exception while retrieving value in ResultSet at [" + i + "]. Continuing.",e);
			}
		}
		
	}
	
	private Object getValueForFieldType(BigInteger generatedValue, Field field) {
		if(field.getType().isAssignableFrom(int.class)) {
			logger.warn("Converting generated value (BigDecimal) to int. May lose information)");
			return generatedValue.intValue();
		}
		if(field.getType().isAssignableFrom(short.class)) {
			logger.warn("Converting generated value (BigDecimal) to short. May lose information)");
			return generatedValue.shortValue();
		}
		if(field.getType().isAssignableFrom(long.class)) {
			return generatedValue.longValue();
		}
		logger.info("GeneratedValue (BigDecimal) was not int,short or long. Returning double value");
		return generatedValue.doubleValue();
	}
	
	private Object getValueForFieldType(BigDecimal generatedValue, Field field) {
		if(field.getType().isAssignableFrom(int.class)) {
			logger.warn("Converting generated value (BigDecimal) to int. May lose information)");
			return generatedValue.intValue();
		}
		if(field.getType().isAssignableFrom(short.class)) {
			logger.warn("Converting generated value (BigDecimal) to short. May lose information)");
			return generatedValue.shortValue();
		}
		if(field.getType().isAssignableFrom(long.class)) {
			return generatedValue.longValue();
		}
		logger.info("GeneratedValue (BigDecimal) was not int,short or long. Returning double value");
		return generatedValue.doubleValue();
	}

	/**
	 * Inspects the given class for fields to map from the ResultSet to an actual instance of the class.
	 * If the given resultSet is null, this method will return an empty list. The method only considers fields
	 * that have been annotated with the Column Annotation.
	 * @param resultSet - ResultSet that should be used when mapping the fields who are marked with the Column annotation.
	 * @return List that contains instances that have been mapped of the given class.
	 * @throws ResultMapException Thrown when a error  occurs while trying to map a field.
	 */
	@Override
	public List<E> toResultList(ResultSet resultSet) throws ResultMapException {
		List<E> resultList = new LinkedList<E>();
		if(resultSet == null) {
			logger.warn("ResultSet is null. Returning empty list");
			return resultList;
		}
		try {
			if(resultSet.isClosed()) {
				logger.warn("ResultSet is closed. Returning empty list.");
				return resultList;
			}
		} catch (SQLException e) {
			logger.debug("Failed while checking whether the ResultSet is closed",e);
			return resultList;
		}
		
		try {
			while(resultSet.next()) {
				E instance = toResult(resultSet);
				resultList.add(instance);
			}
		} catch (SQLException e) {
			throw new ResultMapException("Failed to map an instance while traversing the ResultSet",e);
		}
		return resultList;
	}
	
	/**
	 * Creates a Map that contains mapped results based on the ResultSet. Each entry in the map is inserted into the map
	 * based on the value of the field that is marked with the @{@link Key} annotation. The key along with the corresponding
	 * instance is inserted into the map. Only the first field marked with the @Key annotation is considered.
	 * @param ResultSet ResultSet that must be used to map results from
	 * @param sorted Determines whether the Map should be sorted or not
	 * @return Map containing all the map results from the ResultSet
	 * @throws ResultMapException Thrown when a error occurs mapping a result from the ResultSet
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <K> Map<K, E> toResultMap(ResultSet resultSet, boolean sorted) throws ResultMapException {
		Map<K,E> resultMap = null;
		if( sorted ) {
			resultMap = new TreeMap<K,E>();
		} else {
			resultMap = new HashMap<K,E>();
		}
		if(resultSet == null) {
			return resultMap;
		}
		try {
			if(resultSet.isClosed()) {
				return resultMap;
			}
		} catch (SQLException e) {
			logger.debug("Failed while checking whether the ResultSet is closed",e);
			return resultMap;
		}
		logger.debug("Searching " + this.classInspector.getInspectedClass().getCanonicalName() + " for fields that are marked with @Key annotation");
		Field keyField = classInspector.findFieldWithAnnotation(Key.class);
		if(keyField == null) {
			throw new ResultMapException("Could not find a field annotated with the @Key annotation in " + classInspector.getInspectedClass().getName());
		}
		logger.debug("Field<" + keyField.getName() + " is marked with the @Key");
		Method keyGetMethod = classInspector.findGetMethod(keyField);
		logger.debug("Using get method<" + keyGetMethod.getName() + "> to get value of Key");
		try {
			while(resultSet.next()) {
				E instance = toResult(resultSet);
				K key = (K) classInspector.executeGetMethod(instance, keyGetMethod);
				logger.debug("Inserting mapped result with key<" + key + " into map");
				resultMap.put(key ,instance);
			}
		} catch (SQLException e) {
			throw new ResultMapException("Failed to map an instance while traversing the ResultSet",e);
		}
		return resultMap;
	}

	/**
	 * Maps the values found in the ResultSet to a instance of whatever type this DefaultQueryResultMapper was instantiated with.
	 * Only fields that are marked with {@link Column} or {@link Embedded} are considered when performing a mapping.
	 * @param resultSet ResultSet from which values should be mapped to fields in the instance
	 * @return E Instance with fields mapped
	 * @throws ResultMapException Thrown when one of the fields fail to map.
	 */
	@Override
	public E toResult(ResultSet resultSet) throws ResultMapException {
		return toResult(classInspector,resultSet);
	}
	
	private E toResult(ClassInspect classInspector, ResultSet resultSet) throws ResultMapException {
		E instance = classInspector.<E>createNewInstanceOfClass();
		mapColumnsToInstance(resultSet,classInspector,instance);
		mapEmbeddedInInstance(resultSet,classInspector,instance);
		return instance;
	}

	private void mapEmbeddedInInstance(ResultSet resultSet,ClassInspect parentInspector, E instance) throws ResultMapException {
		List<Field> annotatedFields = parentInspector.findAllAnnotatedFields(Embedded.class);
		ClassInspect embeddedClassInspector = null;
		for(Field field : annotatedFields) {
			logger.debug("Mapping field<" + field.getName() + "> which is marked with the @Embedded annotation");
			embeddedClassInspector = new ClassInspect(field.getType());
			Object embeddedInstance = toResult(embeddedClassInspector,resultSet);
			Method setMethod = parentInspector.findSetMethod(field, field.getType());
			logger.debug("Executing set method <" + setMethod == null ? "null" : setMethod.getName() + ">");
			parentInspector.executeSetMethod(instance,setMethod,embeddedInstance);
		}
	}
	
	private void mapColumnsToInstance (ResultSet resultSet, ClassInspect parentInspector , Object instance) throws ResultMapException {
		List<Field> annotatedFields = parentInspector.findAllAnnotatedFields(Column.class);
		for(Field field : annotatedFields) {
			String columnName = field.getAnnotation(Column.class).name();
			logger.debug("Mapping field<" + field + "> that is marked with @Column(name=" + columnName + ").");
			try {
				Object value = resultSet.getObject(columnName);
				Method setMethod = parentInspector.findSetMethod(field, field.getType());
				logger.debug("Executing set method <" + setMethod == null ? "null" : setMethod.getName() + ">");
				parentInspector.executeSetMethod(instance,setMethod,value);
			} catch (SQLException e) {
				handleMappingSQLException(e, columnName);
			}
		}
	}
	
	protected void handleMappingSQLException(SQLException e, String columnName) throws ResultMapException {
		if(strict) {
			throw new ResultMapException("Failed to retrieve object for column [" + columnName + "] in ResultSet", e);
		} else {
			logger.warn("Failed to retrieve object for column [" + columnName + "] in ResultSet. Continuing.");
		}
	}
}
