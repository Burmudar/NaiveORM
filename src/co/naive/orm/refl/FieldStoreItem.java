package co.naive.orm.refl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 *
 * @author Burmudar
 */
public class FieldStoreItem {
    private String key;
    private Field field;
    private Method setMethod;
    private Method getMethod;
    private Class<? extends Annotation> annotation;
    
    public FieldStoreItem(Field field) {
        this.field = field;
        setKey(createKey(field));
    }
    
    public static String createKey(Field field) {
        return field.getName();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
        setKey(field.getName());
    }

    public Method getSetMethod() {
        return setMethod;
    }

    public void setSetMethod(Method setMethod) {
        this.setMethod = setMethod;
    }

    public Method getGetMethod() {
        return getMethod;
    }

    public void setGetMethod(Method getMethod) {
        this.getMethod = getMethod;
    }

    public Class<? extends Annotation> getAnnotation() {
        return annotation;
    }

    public void setAnnotation(Class<? extends Annotation> annotation) {
        this.annotation = annotation;
    }
    
    
    
}
