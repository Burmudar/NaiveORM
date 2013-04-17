package co.naive.orm.db.generate;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import co.naive.orm.db.query.PreQueryAdapter;
import co.naive.orm.refl.ClassInspect;



public class InsertParameterAdapter implements PreQueryAdapter{
	private List<Entry<Integer, Object[]>> parameters;
	private static Log logger = LogFactory.getLog(InsertParameterAdapter.class);
	private ClassInspect classInspect;
	public List<Entry<Integer, Object[]>> getParameters() {
		return parameters;
	}

	public void setParameters(List<Entry<Integer, Object[]>> parameters) {
		this.parameters = parameters;
	}

	public InsertParameterAdapter(List<Entry<Integer, Object[]>> parameters) {
		this.parameters = parameters;
	}
	
	protected ClassInspect getClassInspect() {
		return classInspect;
	}
	
	protected void setClassInspect(ClassInspect inspect) {
		classInspect = inspect;
	}
	

	public InsertParameterAdapter() {
		super();
		setClassInspect(new ClassInspect(PreparedStatement.class));
		setParameters(new LinkedList<Entry<Integer,Object[]>> ());
	}

	@Override
	public void setParametersOn(PreparedStatement statement) throws SQLException {
		logger.info("Setting INSERT parameters on prepared statement ...");
		int index = 0;
		Object[] valueSet = null;
		for(Entry<Integer, Object[]> entry : getParameters()) {
			index = entry.getKey();
			valueSet = entry.getValue();
			Object valueFieldInstance = valueSet[1];
			if(valueFieldInstance == null) {
				statement.setNull(index, Types.NULL);
				logger.info("<parameters> Setting parameter at <" + index + "> with value <null>");
			} else if (valueFieldInstance instanceof Integer) {
				Integer intValue = (Integer)valueFieldInstance;
				statement.setInt(index, intValue.intValue());
				logger.info("<parameters> Setting parameter at <" + index + "> with value <" +intValue + ">" );
			} else if (valueFieldInstance instanceof Boolean) {
				Boolean boolValue = (Boolean)valueFieldInstance;
				statement.setBoolean(index, (boolean) boolValue);
				logger.info("<parameters> Setting parameter at <" + index + "> with value <" + boolValue + ">");
			} else {
				setNormalFieldOnPreparedStatement(statement, entry);
			}
		}
		logger.info("Setting INSERT parameters on prepared statement ... Complete");
		
	}

	private void setNormalFieldOnPreparedStatement(PreparedStatement statement, Entry<Integer, Object[]> paramEntry) {
		int index = paramEntry.getKey();
		Object[] valueSet = paramEntry.getValue();
		Field valueField = (Field) valueSet[0];
		Object valueFieldInstance = valueSet[1];
		Class<?> valueClass = valueField.getType();
		Method setMethod = getClassInspect().findMethod("set", valueClass.getSimpleName(), int.class, valueClass);
		try {
			setMethod.invoke(statement, index,valueClass.cast(valueFieldInstance));
			logger.info("<parameters> Setting parameter at <" + index + "> with value <" + valueClass.cast(valueFieldInstance).toString() + ">");
		} catch (IllegalArgumentException e) {
			logger.error("<parameters> Illegal argument was passed to PreparedStatement method<" + setMethod != null ? setMethod.getName() : "null" + ">");
			logger.info("<parameters> Error occured while setting a value on the prepared statement. Continuing.");
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			logger.error("<parameters> Illegal access occured on PreparedStatement method<" + setMethod != null ? setMethod.getName() : "null" + ">");
			logger.info("<parameters> Error occured while setting a value on the prepared statement. Continuing.");
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			logger.error("An exception got thrown while invoking method <" + setMethod != null ? setMethod.getName() : "null" + "> through reflection. Exception:\n "  + e.getMessage());
		}
	}
	

}
