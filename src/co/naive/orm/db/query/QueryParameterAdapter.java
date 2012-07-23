package co.naive.orm.db.query;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface QueryParameterAdapter {
	void setParametersOn(PreparedStatement statement) throws SQLException;
}
