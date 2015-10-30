package akj.apiautomation;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class TestConfiguration {
	private static TestConfiguration instance;
	private Properties config;
	private String sheetPath;

	public Properties getConfig() {
		return config;
	}

	Logger log = Logger.getLogger(this.getClass());

	private TestConfiguration() {
		try {
			FileInputStream fin = new FileInputStream("resources/config");
			config = new Properties();
			config.load(fin);
			sheetPath = config.getProperty("request_sheet");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static TestConfiguration getInstance() {
		if (null == instance) {
			instance = new TestConfiguration();
		}
		return instance;
	}

	public String getSheetPath() {
		return sheetPath;
	}

	public void setSheetPath(String sheetPath) {
		this.sheetPath = sheetPath;
	}
}
