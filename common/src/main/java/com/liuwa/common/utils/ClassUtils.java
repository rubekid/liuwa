package com.liuwa.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

/**
 * 类的相关实现
 * 
 * @author lazier
 */
@SuppressWarnings("rawtypes")
public class ClassUtils {

	private static Logger logger = LoggerFactory.getLogger(ClassUtils.class);

	/**
	 * 根据字段名称获取字段
	 * @param obj
	 * @param fieldName
	 * @return
	 */
	public static Field getField(Object obj, String fieldName){

		Class clazz = obj.getClass();
		return getField(clazz, fieldName);
	}

	/**
	 * 根据字段名称获取字段
	 * @param clazz
	 * @param fieldName
	 * @return
	 */
	public static Field getField(Class clazz, String fieldName){
		while (clazz != null){
			Field[] fields = clazz.getDeclaredFields();
			for(Field field : fields){
				if(field.getName().equals(fieldName)){
					return field;
				}
			}
			clazz = clazz.getSuperclass();
		}
		return null;
	}

	/**
	 * 返回某个父类底下所有的实现类
	 */
	@SuppressWarnings("unchecked")
	public static List<Class> getSubClassesByParentClass(Class parentClass, String packageName) {

		List<Class> returnClassList = new ArrayList<Class>();
		if(packageName == null || "".equals(packageName)){
			packageName = parentClass.getPackage().getName();
		}
		
		try {
			List<Class> allClass = getClassesByPackageName(packageName);
			for (int i = 0; i < allClass.size(); i++) {
				if (parentClass.isAssignableFrom(allClass.get(i))) {
					if (!parentClass.equals(allClass.get(i))) {
						returnClassList.add(allClass.get(i));
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return returnClassList;
	}

	/**
	 * 从一个包中查找出所有类,在jar包中不能查找
	 * 
	 * @param packageName
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static List<Class> getClassesByPackageName(String packageName)
			throws IOException, ClassNotFoundException {
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = new ArrayList<File>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			dirs.add(new File(resource.getFile()));
		}
		ArrayList<Class> classes = new ArrayList<Class>();
		for (File directory : dirs) {
			classes.addAll(findClasses(directory, packageName));
		}
		return classes;
	}

	/**
	 * 
	 * @param directory
	 * @param packageName
	 * @return
	 * @throws ClassNotFoundException
	 */
	private static List<Class> findClasses(File directory, String packageName)
			throws ClassNotFoundException {
		List<Class> classes = new ArrayList<Class>();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				assert !file.getName().contains(".");
				classes.addAll(findClasses(file, packageName + '.'
						+ file.getName()));
			} else if (file.getName().endsWith(".class")) {
				classes.add(Class.forName(packageName
						+ "."
						+ file.getName().substring(0,
								file.getName().length() - 6)));
			}
		}
		return classes;
	}

	/**
	 * 对Map类型数据按Key排序
	 * 
	 * @param map
	 */
	@SuppressWarnings("unchecked")
	public static List sort(Map map) {
		List list = new ArrayList(map.keySet());
		Collections.sort(list, new Comparator() {
			public int compare(Object a, Object b) {
				return a.toString().toLowerCase().compareTo(
						b.toString().toLowerCase());
			}
		});
		return list;
	}

	/**
	 * 泛型实例化
	 * @param t
	 * @param <T>
	 * @return
	 */
	public static <T> T getInstance(T t){
		try{
			t = (T) t.getClass().newInstance();
		}
		catch (InstantiationException | IllegalArgumentException | IllegalAccessException  ex){
			logger.error(ex.getMessage(), ex);
		}
		return t;
	}

	/**
	 * 通过方法名获取方法对象
	 * @param obj
	 * @param methodName
	 * @return
	 */
	public static Method findMethod(Object obj, String methodName){
		Method[] methods = obj.getClass().getDeclaredMethods();
		for(Method method : methods){
			if(method.getName().equals(methodName)){
				return method;
			}
		}
		return null;
	}

	/**
	 * 根据注解获取方法数组
	 * @param clazz
	 * @param annotation
	 * @return
	 */
    public static Method[] getDeclaredMethods(Class<?> clazz, Class<? extends Annotation> annotation) {
    	Method[] methods = clazz.getDeclaredMethods();
    	List<Method> result = new ArrayList<Method>();
    	for(Method method : methods){
    		if(method.isAnnotationPresent(annotation)){
    			result.add(method);
			}
		}
    	return result.toArray(new Method[result.size()]);
    }

	/**
	 * 根据方法名获取方法数组
	 * @param clazz
	 * @param methodNames
	 * @return
	 */
	public static Method[] getDeclaredMethods(Class<?> clazz, String ... methodNames){
		Method[] methods = clazz.getDeclaredMethods();
		List<Method> result = new ArrayList<Method>();
		List<String> methodNameList = Arrays.asList(methodNames);

		for(Method method : methods){
			if(methodNameList.contains(method.getName())){
				result.add(method);
			}
		}
		return result.toArray(new Method[result.size()]);
	}

	/**
     * 扫描包名，根据注解获取类列表
	 * @param packageName
     * @param annotation
     * @return
     */
	public static List<Class> findClassesByAnnotation(String packageName, Class<? extends Annotation> annotation ){
		List<Class> classes = new ArrayList<Class>();
		try{
			List<Class> list = getClassesByPackageName(packageName);
			for(Class item : list){
				if(item.isAnnotationPresent(annotation)){
					classes.add(item);
				}
			}
		}
		catch (IOException | ClassNotFoundException ex){
			logger.error(ex.getMessage(), ex);
		}
		return classes;



	}
}