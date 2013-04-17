package co.naive.orm.db.query;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import co.naive.orm.db.exception.ResultMapException;


public interface ResultProducer<E> {

	public abstract E toResult(ResultSet resultSet) throws ResultMapException;

	public abstract List<E> toResultList(ResultSet resultSet)
			throws ResultMapException;

	public abstract <K> Map<K, E> toResultMap(ResultSet resultSet,
			boolean sorted) throws ResultMapException;

}