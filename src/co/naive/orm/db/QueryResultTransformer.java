package co.naive.orm.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface QueryResultTransformer<E> {
	List<E> toResultList(ResultSet resultSet) throws SQLException;
}
