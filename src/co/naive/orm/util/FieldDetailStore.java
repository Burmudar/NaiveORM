/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package co.naive.orm.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Burmudar
 */
public class FieldDetailStore {
    private Map<String,FieldStoreItem> store;
    private List<Field> fields;
    public FieldDetailStore() {
        store = new HashMap<String, FieldStoreItem>();
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }
    
    public void addAllFields(Field[] fields) {
        for(Field field : fields) {
            add(field);
            getFields().add(field);
        }
    }
    
    public void addAllFields(List<Field> fields) {
        for(Field field : fields) {
            add(field);
            getFields().add(field);
        }
    }
    
    public void add(Field field) {
        if(field == null) {
            throw new IllegalArgumentException("Field cannot be null");
        }
        FieldStoreItem item = new FieldStoreItem(field);
        store.put(item.getKey(), item);
    }
    
    public boolean isGetMethodPresentFor(Field field) {
        FieldStoreItem item = store.get(FieldStoreItem.createKey(field));
        if(item != null) {
            return item.getGetMethod() != null ? true : false;
        }
        return false;
    }
    
    public boolean isSetMethodPresentFor(Field field) {
        FieldStoreItem item = store.get(FieldStoreItem.createKey(field));
        if(item != null) {
            return item.getSetMethod() != null ? true : false;
        }
        return false;
    }
    
    public void addGetMethod(Field field, Method method) {
        FieldStoreItem item = store.get(FieldStoreItem.createKey(field));
        if(item != null) {
            item.setGetMethod(method);
        } else {
            item = new FieldStoreItem(field);
            item.setGetMethod(method);
            store.put(item.getKey(),item);
        }
    }
    
    public void addSetMethod(Field field, Method method) {
        FieldStoreItem item = store.get(FieldStoreItem.createKey(field));
        if(item != null) {
            item.setSetMethod(method);
        } else {
            item = new FieldStoreItem(field);
            item.setSetMethod(method);
            store.put(item.getKey(),item);
        }
    }
    
    public void addAnnotation(Field field, Class<? extends Annotation> annotation) {
        FieldStoreItem item = store.get(FieldStoreItem.createKey(field));
        if(item != null) {
            item.setAnnotation(annotation);
        } else {
            item = new FieldStoreItem(field);
            item.setAnnotation(annotation);
            store.put(item.getKey(),item);
        }
    }
    
    public Method setMethodOf(Field field) {
        FieldStoreItem item = store.get(FieldStoreItem.createKey(field));
        if(item != null) {
            return item.getGetMethod();
        }
        return null;
    }
    
    public Method getMethodOf(Field field) {
        FieldStoreItem item = store.get(FieldStoreItem.createKey(field));
        if(item != null) {
            return item.getSetMethod();
        }
        return null;
    }
    
    public Class<? extends Annotation> annotationOf(Field field) {
        FieldStoreItem item = store.get(FieldStoreItem.createKey(field));
        if(item != null) {
            return item.getAnnotation();
        }
        return null;
    }
}
