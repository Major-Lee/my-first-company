package com.bhu.statistics.util;

import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;


public class ConfigManager {

	private static final Logger log = new Logger(ConfigManager.class);
	private static ConfigManager configManger = new ConfigManager();

	private static final String defaultFileName = "bhu";

	private volatile static Map<String, Properties> proMap = new ConcurrentHashMap<String, Properties>();

	private ConfigManager() {

	}

	public static ConfigManager instance() {
		return configManger;
	}

	/**
	 * 
	 * @param profileName
	 *            配置文件名（不包括后缀）
	 * @param key
	 *            属性key值
	 * @return
	 */
	public String getProperty(String profileName, String key) {
		if (key == null || profileName == null) {
			throw new IllegalArgumentException("key is null");
		}
		Properties properties = proMap.get(profileName);

		if (properties == null) {
			synchronized (this) {

				if (properties == null) {

					properties = this.loadProps(profileName);

					if (properties != null) {
						proMap.put(profileName, properties);
					} else {
						return null;
					}
				}
			}
		}

		String value = properties.getProperty(key);
		return value;
	}

	public void clearProps(String proFileName) {
		proMap.remove(proFileName);
	}

	public String getProperty(String key) {
		return getProperty(defaultFileName, key);
	}

	/**
	 * 
	 * @param profileName
	 *            配置文件名（不包括后缀）
	 * @param key
	 *            属性key值
	 * @return
	 */
	public int getIntProperty(String profileName, String key) {
		if (key == null || profileName == null) {
			throw new IllegalArgumentException("key is null");
		}

		String intValue = this.getProperty(profileName, key);
		try {
			return Integer.parseInt(intValue);
		} catch (Exception e) {
			// e.printStackTrace();
			log.error(profileName + "." + key + "=" + intValue + " has exeception:" + e.toString());
			return -1;
		}
	}

	public int getIntProperty(String key) {
		return getIntProperty(defaultFileName, key);
	}

	public boolean getBooleanProperty(String key) {
		return getBooleanProperty(defaultFileName, key);
	}

	/**
	 * 获取一个boolean型的属性值，出错默认返回false
	 */
	public boolean getBooleanProperty(String profileName, String name) {

		// 先获得String型值，然后转换成boolean型
		String value = this.getProperty(profileName, name);

		if (value == null)
			return false;

		return (new Boolean(value)).booleanValue();
	}

	public Properties loadProps(String proFileName) {

		log.debug("Getting Config");

		Properties properties = null;

		InputStream in = null;

		try {
			properties = new Properties();
			if (proFileName != null && proFileName.startsWith("http:")) {
				URL url = new URL(proFileName);
				in = url.openStream();
			} else {
				String fileName = "/" + proFileName + ".properties";
				in = getClass().getResourceAsStream(fileName);
//				properties.load(in);
			}
			properties.load(in);
//			log.info("Successfully  proFileName=" + proFileName + " load Properties:" + properties);

		} catch (Exception e) {
			e.printStackTrace();
			log.error("Error reading " + proFileName + " in loadProps() " + e.getMessage());
			log.error("Ensure the " + proFileName + " file is readable and in your classpath.");
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return properties;
	}
}