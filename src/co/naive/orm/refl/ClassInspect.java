package co.naive.orm.refl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;


public class ClassInspect {
	private Field[] fields;
	private Class<?> inspectedClass;
    private FieldDetailStore fieldDetailStore;

    public FieldDetailStore getFieldDetailStore() {
        return fieldDetailStore;
    }

    public void setFieldDetailStore(FieldDetailStore fieldDetailStore) {
        this.fieldDetailStore = fieldDetailStore;
    }
        
	public Field[] getFields() {
		return fields;
	}
	public void setFields(Field[] fields) {
		this.fields = fields;
	}
	
	public Class<?> getInspectedClass() {
		return inspectedClass;
	}
	public void setInspectedClass(Class<?> inspectedClass) {
		this.inspectedClass = inspectedClass;
	}
	
	public ClassInspect(Class<?> objectClass) {
        setFieldDetailStore(new FieldDetailStore());
		setInspectedClass(objectClass);
		setFields(objectClass.getDeclaredFields());
	}
	
	private String createMethodName(String prefix, String fieldName) {
		StringBuilder builder = new StringBuilder(fieldName);
		Character firstChar = fieldName.charAt(0);
		builder.setCharAt(0, Character.toUpperCase(firstChar));
		builder.insert(0, prefix);
		return builder.toString();
	}
	
	@SuppressWarnings("unchecked")
	public <E> E createNewInstanceOfClass() {
		try {
			return ((E) this.getInspectedClass().newInstance());
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Method findMethod(String prefix, String methodName, Class<?> ... paramType) {
		if(methodName == null || methodName.isEmpty()) 
			return null;
		methodName = createMethodName(prefix, methodName);
		try {
			return getInspectedClass().getDeclaredMethod(methodName, paramType);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Method findGetMethod(Field field)  {
		if(field == null)
                    return null;
                if(getFieldDetailStore().isGetMethodPresentFor(field)) {
                    return getFieldDetailStore().getMethodOf(field);
                }
		String getterName = createMethodName("get", field.getName());
                
		try {
			Method getMethod = getInspectedClass().getDeclaredMethod(getterName);
                        getFieldDetailStore().addSetMethod(field,getMethod);
			return getMethod;
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			System.err.println("<Classinspect> Did not find GET method <" + getterName + ">. Trying to find <is> method variant");
			return findIsMethod(field);
		}
		return null;
	}
	
	public Method findIsMethod(Field field) {
		if(field == null)
			return null;
                if(getFieldDetailStore().isSetMethodPresentFor(field)) {
                    return getFieldDetailStore().setMethodOf(field);
                }
		String getterName = createMethodName("is", field.getName());
                
		try {
                        Method getMethod = getInspectedClass().getDeclaredMethod(getterName);
                        getFieldDetailStore().addSetMethod(field,getMethod);
			return getMethod;
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Method findSetMethod(Field field, Class<?> paramType) {
		if(field == null)
			return null;
                if(getFieldDetailStore().isSetMethodPresentFor(field)) {
                    return getFieldDetailStore().setMethodOf(field);
                }
		String setterName = createMethodName("set", field.getName());
		try {
			Method setMethod = getInspectedClass().getDeclaredMethod(setterName, paramType);
                        getFieldDetailStore().addSetMethod(field,setMethod);
                        return setMethod;
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Annotation findAnnotation(Class<? extends Annotation> annotationClass) {
		Annotation annotation = getInspectedClass().getAnnotation(annotationClass);
		return annotation;
	}
	
	public Field findFieldWithAnnotationAttributeAndValue(Class<? extends Annotation> annotationClass,String attributeName, String attributeValue) {
		for(Field field : getFields()) {
			if(field.isAnnotationPresent(annotationClass)) {
				Annotation annotation = field.getAnnotation(annotationClass);
				try {
					Method nameMethod = annotationClass.getDeclaredMethod(attributeName);
					Object invokeResult = nameMethod.invoke(annotation);
					if(attributeValue.matches((String) invokeResult)) {
                                                getFieldDetailStore().add(field);
                                                getFieldDetailStore().addAnnotation(field, annotationClass);
						return field;
                                        } else {
						continue;
                                        }
				} catch (IllegalArgumentException e) {
					System.err.println("<ClassInspect> Failed to execute Annotation Method <" + attributeName +">. Method should accept no arguments.");
				} catch (IllegalAccessException e) {
					System.err.println("<ClassInspect> Failed to execute Annotation Method <" + attributeName +">. Method should be public");
				} catch (InvocationTargetException e) {
					System.err.println("<ClassInspect> Failed to execute Annotation Method <" + attributeName +">. Exception: \n" + e.getMessage());
				} catch (SecurityException e) {
					System.err.println("<ClassInspect> Failed to execute Annotation Method <" + attributeName +">. Exception: \n" + e.getMessage());
				} catch (NoSuchMethodException e) {
					System.err.println("<ClassInspect> Failed to execute Annotation Method <" + attributeName +">. Method was not found on <class : " + annotationClass.getName() + ">");
				}
			}
		}
		return null;
	}
	
	public void executeSetMethod(Object instance, Method method, Object value) {
		if(method == null)
			return;
		try {
			method.invoke(instance, value);
		} catch (IllegalArgumentException e) {
			System.err.println("<ClassInspect> Failed to execute SET Method <" + method.getName() + ">. Arguments passed to method is wrong.");
		} catch (IllegalAccessException e) {
			System.err.println("<ClassInspect> Failed to execute SET Method <" + method.getName() + ">. Method isn't marked as public.");
		} catch (InvocationTargetException e) {
			System.err.println("<ClassInspect> Failed to execute SET Method <" + method.getName() + ">. Exception: \n" + e.getMessage());
		}
	}
	
	@SuppressWarnings("unchecked")
	public <E> E  executeGetMethod(E instance, Method method) {
		if(method == null)
			return null;
		try {
			return (E) method.invoke(instance);
		} catch (IllegalArgumentException e) {
			System.err.println("<ClassInspect> Failed to execute GET Method <" + method.getName() + ">. Method should accept no arguments.");
		} catch (IllegalAccessException e) {
			System.err.println("<ClassInspect> Failed to execute GET Method <" + method.getName() + ">. Method isn't marked as public.");
		} catch (InvocationTargetException e) {
			System.err.println("<ClassInspect> Failed to execute GET Method <" + method.getName() + ">. Exception: \n" + e.getMessage());
		}
		return null;
	}
	
	public List<Field> findAllAnnotatedFields(Class<? extends Annotation> annotationClass) {
		List<Field> annotatedFields = new LinkedList<Field>();
		for(Field field : getFields()) {
			if(field.isAnnotationPresent(annotationClass)) {
				annotatedFields.add(field);
			}
		}
        getFieldDetailStore().addAllFields(annotatedFields);
		return annotatedFields;
	}
	
	public Field findFieldWithAnnotation(Class<? extends Annotation> annotationClass) {
		for(Field field : getFields()) {
			if(field.isAnnotationPresent(annotationClass)) {
				return field;
			}
		}
		return null;
	}
	
        public static void main(String args[]) {
            ClassInspect ci = new ClassInspect(FieldStoreItem.class);
            Field[] ciFields = ci.getFields();
            for(Field field : ciFields) {
                long now = System.currentTimeMillis();
                ci.findGetMethod(field);
                long timeTaken = System.currentTimeMillis() - now;
                System.out.println("Took [" + timeTaken + "] to find <" + field.getName() + "> GET method <1st try>");
                now = System.currentTimeMillis();
                ci.findGetMethod(field);
                timeTaken = System.currentTimeMillis() - now;
                System.out.println("Took [" + timeTaken + "] to find <" + field.getName() + "> GET method <2st try>");
                now = System.currentTimeMillis();
                ci.findSetMethod(field, field.getType());
                timeTaken = System.currentTimeMillis() - now;
                System.out.println("Took [" + timeTaken + "] to find <" + field.getName() + "> SET method <1st try>");
                now = System.currentTimeMillis();
                ci.findSetMethod(field, field.getType());
                timeTaken = System.currentTimeMillis() - now;
                System.out.println("Took [" + timeTaken + "] to find <" + field.getName() + "> SET method <2st try>");
            }
        }
	
}
