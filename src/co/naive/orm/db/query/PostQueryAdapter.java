package co.naive.orm.db.query;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface PostQueryAdapter {
	public void process(ResultSet resultSet) throws SQLException;
}
