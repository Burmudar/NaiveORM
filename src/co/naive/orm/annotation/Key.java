package co.naive.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import co.naive.orm.db.DefaultQueryResultMapper;


/**
 * <p>
 * A decorator annotation. The annotation is used to mark fields which are to be used as keys in a map.
 * It only makes sense to have one key per class. If more than one field is to be used as key then
 * one needs to decide how you are going to represent the composite of the keys ?
 * </p>
 * <p>
 * Primarily, this annotation is only used when maps needs to be created from a list of objects, as can
 * be seen in the {@link DefaultQueryResultMapper#toResultMap(java.sql.ResultSet, boolean)} method.
 * </p>
 * @author William Bezuidenhout
 *
 */
@Retention(value=RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Key {

}
