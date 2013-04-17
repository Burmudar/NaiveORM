package co.naive.orm.db;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import co.naive.orm.db.exception.ResultMapException;

/**
 * <p>
 * Interface that defines all the methods required to do various mappings.
 * All Queries use classes that implement QueryResultTransformer to do various mappings.
 * Typically Select queries use the to{Result,ResultList,ResultMap} methods.
 * The mapGenerated should only be used by Update or Insert Queries that need
 * to have auto generated database values mapped.
 * </p>
 * @author William Bezuidenhout (bbdnet1030)
 *
 * @param <E>
 */
public interface QueryResultTransformer<E> {
	void useStrictMapping();
	void useLenientMapping();
	void mapGenerated(E instance, ResultSet resultSet) throws ResultMapException;
	void mapGenerated(List<E> instances, ResultSet resultSet) throws ResultMapException;
	List<E> toResultList(ResultSet resultSet) throws ResultMapException;
	E toResult(ResultSet resultSet) throws ResultMapException;
	<K> Map<K, E> toResultMap(ResultSet resultSet, boolean sorted)
			throws ResultMapException;
}
