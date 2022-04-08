package com.liuwa.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * 属性加载
 * @author Rubekid
 *
 * 2017年5月18日 下午3:09:24
 */
public class PropertiesLoader {

	private static Logger logger = LoggerFactory.getLogger(PropertiesLoader.class);


	/**
	 * 载入多个文件, 文件路径使用Spring Resource格式.
	 */
	public static Properties loadProperties(String... resourcesPaths) {
		Properties props = new Properties();

		for (String resourcesPath : resourcesPaths) {
			if(resourcesPath.endsWith(".yml")){
				loadYml(props, resourcesPath);
			}
			else{
				loadProperty(props, resourcesPath);
			}
		}
		return props;
	}

	/**
	 * 加载yml配置文件
	 * @param props
	 * @param ymlPath
	 */
	private static void loadYml(Properties props, String ymlPath) {
		try{
            Object pathResource = null;
			String userDir = System.getProperty("user.dir");
			String path = userDir + File.separator + "config" + File.separator + ymlPath;
			File configFile = new File(path);
			if(!configFile.exists()){
				path = userDir + File.separator + ymlPath;
				configFile = new File(path);
			}

			if(!configFile.exists()){
				Class classPathResource = Class.forName("org.springframework.core.io.ClassPathResource");
				Constructor classPathConstructor = classPathResource.getConstructor(String.class);
				Method exists = classPathResource.getMethod("exists");
				path = "config" + File.separator + ymlPath;
				logger.info("path : {}", path);
				pathResource = classPathConstructor.newInstance(path);
				if(!(boolean)exists.invoke(pathResource)){
					path = ymlPath;
                    logger.info("path : {}", path);
					pathResource = classPathConstructor.newInstance(path);
					if(!(boolean)exists.invoke(pathResource)){
						logger.error("Could not load yml from path:" + ymlPath );
						return ;
					}
				}
			}
			else{
				Class fileSystemResource = Class.forName("org.springframework.core.io.FileSystemResource");
				Constructor fileSystemConstructor = fileSystemResource.getConstructor(String.class);
                pathResource = fileSystemConstructor.newInstance(path);
            }
			logger.info("ymal path : {}", path);
            Class yamlPropertiesFactoryBean = Class.forName("org.springframework.beans.factory.config.YamlPropertiesFactoryBean");
            Object yaml = yamlPropertiesFactoryBean.newInstance();
            Class baseResource = Class.forName("org.springframework.core.io.Resource");


            Method setResources = yamlPropertiesFactoryBean.getMethod("setResources", Array.newInstance(baseResource, 1).getClass());
            Method getObject = yamlPropertiesFactoryBean.getMethod("getObject");

            Object[] resources = (Object[]) Array.newInstance(baseResource, 1);
            resources[0] = pathResource;
            setResources.invoke(yaml, new Object[]{resources});

            props.putAll((Properties)getObject.invoke(yaml));
		}
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ex){

			logger.error("Could not load yml from path: {} , {}", ymlPath, ex.getMessage(), ex);
		}
	}

	/**
	 * 加载配置文件
	 * @param props
	 * @param resourcesPath
	 */
	private static void loadProperty(Properties props, String resourcesPath)  {
		InputStream is = null;
		try {
			String userDir = System.getProperty("user.dir");
			File configFile = new File(userDir + File.separator + "config" + File.separator + resourcesPath);
			if(!configFile.exists()){
				configFile = new File(userDir + File.separator + resourcesPath);
			}

			if(configFile.exists()){
				is = new BufferedInputStream(new FileInputStream(configFile));
			}
			else{
				String path = "config" + File.separator + resourcesPath;
				is = PropertiesLoader.class.getClassLoader().getResourceAsStream(path);
				if(is == null) {
					path = resourcesPath;
					is = PropertiesLoader.class.getClassLoader().getResourceAsStream(path);
					if(is == null){
						logger.error("Could not load properties from path:" + path );
						return ;
					}
				}

			}
			if(is != null){
				props.load(is);
			}
		} catch (IOException ex) {
			logger.error("Could not load properties from path:" + resourcesPath + ", " + ex.getMessage());
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException ex) {
				logger.error(ex.getMessage(), ex);
			}
		}
	}

	/**
	 * 加载资源文件
	 * @param resourcePath 资源文件路径
	 * @return
	 */
	public static File loadFile(String resourcePath){
		String userDir = System.getProperty("user.dir");
		File configFile = new File(userDir + File.separator + "config" + File.separator + resourcePath);
		if(!configFile.exists()){
			configFile = new File(userDir + File.separator + resourcePath);
		}
		if(!configFile.exists()){
			ClassPathResource classPathResource = new ClassPathResource(resourcePath);
			try{
				configFile = classPathResource.getFile();
			}
			catch (IOException ex){
				logger.error(ex.getMessage(), ex);
			}
		}

		return configFile;
	}
}