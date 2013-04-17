package co.naive.orm.db.query;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface PreQueryAdapter {
	void setParametersOn(PreparedStatement statement) throws SQLException;
}
