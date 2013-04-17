package co.naive.orm.db.query;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DefaultQueryAdapter implements PreQueryAdapter, PostQueryAdapter {
	Object[] parameterValues;

	public DefaultQueryAdapter(Object ... parameterValues) {
		this.parameterValues = parameterValues;
	}
	
	@Override
	public void process(ResultSet resultSet) throws SQLException {

	}

	@Override
	public void setParametersOn(PreparedStatement statement) throws SQLException {
		if( parameterValues == null ) {
			return;
		}
		int i = 1;
		for( Object obj : parameterValues ) {
			if ( obj == null ) {
				statement.setObject(i++, obj);
			}
			if( obj instanceof String ) {
				statement.setString(i++,(String)obj);
			}
			else if (obj instanceof Integer) {
				statement.setInt(i++, (Integer)obj);
			} 
			else if (obj instanceof Long ) {
				statement.setLong(i++, (Long)obj);
			}
			else if (obj instanceof Float || obj instanceof Double) {
				statement.setBigDecimal(i++, new BigDecimal(((Number)obj).longValue()));
			}
			else if (obj instanceof InputStream) {
				statement.setBinaryStream(i++, (InputStream)obj);
			}
			else if (obj instanceof Date){
				statement.setDate(i++, (Date) obj);
			} else if (obj instanceof java.util.Date){
				long t = ((java.util.Date)obj).getTime();
				statement.setDate(i++, new Date(t));
			} else {
				statement.setObject(i++, statement);
			}
		}
	}

}
